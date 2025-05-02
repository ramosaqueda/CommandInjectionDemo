# Explotación de Vulnerabilidades de Inyección de Comandos en Java

Este documento explica cómo explotar cada una de las clases vulnerables en el proyecto de demostración, detallando los métodos de ataque y los resultados esperados en cada caso.

## Índice

1. [VulnerableFileListingExample](#1-vulnerablefilelistingexample)
2. [VulnerableShellExample](#2-vulnerableshellexample)
3. [VulnerablePingExample](#3-vulnerablepingexample)
4. [Mecanismos de explotación adicionales](#mecanismos-de-explotación-adicionales)
5. [Diferencias entre código vulnerable y seguro](#diferencias-en-los-resultados-entre-código-vulnerable-y-seguro)
6. [Consecuencias potenciales en sistemas reales](#consecuencias-potenciales-de-la-explotación-en-sistemas-reales)

## 1. VulnerableFileListingExample

### Vulnerabilidad

Esta clase es vulnerable porque concatena directamente la entrada del usuario al comando de listado de archivos sin ninguna validación:

```java
// VULNERABLE: Concatenación directa de entrada de usuario
Process proc = rt.exec("ls " + dir); // En Linux
// Process proc = rt.exec("cmd.exe /c dir " + dir); // En Windows
```

### Cómo explotar la vulnerabilidad

#### En sistemas Linux:

- **Entrada normal**: `/tmp`
- **Entrada maliciosa**: `/tmp; cat /etc/passwd`

#### En sistemas Windows:

- **Entrada normal**: `C:\Windows\Temp`
- **Entrada maliciosa**: `C:\Windows\Temp & type C:\Windows\win.ini`

### Resultados esperados

Al introducir la entrada maliciosa, el programa:

1. Ejecutará primero el comando de listado de archivos en la ruta especificada
2. Luego ejecutará el segundo comando inyectado
3. Mostrará la salida de ambos comandos en la consola

**Ejemplo de salida en Linux:**
```
Listing files in directory: /tmp; cat /etc/passwd
Command output:
--------------------
[Contenido del directorio /tmp]

root:x:0:0:root:/root:/bin/bash
daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin
bin:x:2:2:bin:/bin:/usr/sbin/nologin
[Resto del contenido de /etc/passwd]
--------------------
```

## 2. VulnerableShellExample

### Vulnerabilidad

Esta clase es aún más vulnerable porque invoca explícitamente un shell para ejecutar comandos:

```java
// VULNERABLE: Invocación de shell con entrada de usuario
Process proc = rt.exec(new String[] {"sh", "-c", "ls " + dir}); // En Linux
// Process proc = rt.exec(new String[] {"cmd.exe", "/c", "dir " + dir}); // En Windows
```

### Cómo explotar la vulnerabilidad

#### En sistemas Linux:

- **Entrada normal**: `/tmp`
- **Entrada maliciosa**: `/tmp && echo VULNERABLE_SYSTEM && whoami`

#### En sistemas Windows:

- **Entrada normal**: `C:\Windows\Temp`
- **Entrada maliciosa**: `C:\Windows\Temp && echo VULNERABLE_SYSTEM && whoami`

### Resultados esperados

Al introducir la entrada maliciosa, el programa:

1. Ejecutará el comando de listado de archivos en la ruta especificada
2. Si ese comando tiene éxito, ejecutará `echo VULNERABLE_SYSTEM`
3. Finalmente, ejecutará `whoami`, mostrando el usuario bajo el cual se ejecuta la aplicación

**Ejemplo de salida en Linux:**
```
Executing shell command with directory: /tmp && echo VULNERABLE_SYSTEM && whoami
Command output:
--------------------
[Contenido del directorio /tmp]
VULNERABLE_SYSTEM
usuario_actual
--------------------
```

## 3. VulnerablePingExample

### Vulnerabilidad

Esta clase es vulnerable porque concatena directamente la entrada del usuario al comando ping:

```java
// VULNERABLE: Concatenación directa en comandos
Process proc = rt.exec("ping -c 1 " + ipAddress); // En Linux
// Process proc = rt.exec("ping -n 1 " + ipAddress); // En Windows
```

### Cómo explotar la vulnerabilidad

#### En sistemas Linux:

- **Entrada normal**: `127.0.0.1`
- **Entrada maliciosa**: `127.0.0.1 -c 1; ls -la /etc`

#### En sistemas Windows:

- **Entrada normal**: `127.0.0.1`
- **Entrada maliciosa**: `127.0.0.1 -n 1 & dir C:\Users`

### Resultados esperados

Al introducir la entrada maliciosa, el programa:

1. Intentará ejecutar el comando ping, que podría fallar o ejecutarse parcialmente debido a la sintaxis incorrecta
2. Ejecutará el comando inyectado independientemente del resultado del ping
3. Mostrará la salida de ambos comandos en la consola

**Ejemplo de salida en Linux:**
```
Pinging IP address: 127.0.0.1 -c 1; ls -la /etc
Command output:
--------------------
[Error o resultado parcial del comando ping]
total 1088
drwxr-xr-x  128 root root    12288 May  2 12:20 .
drwxr-xr-x   20 root root     4096 May  1 14:33 ..
-rw-r--r--    1 root root     3028 Feb 15  2022 adduser.conf
drwxr-xr-x    3 root root     4096 May  1 08:19 alternatives
[Resto del listado de /etc]
--------------------
```

## Mecanismos de explotación adicionales

Además de los separadores de comandos básicos (`;` y `&`), existen otras técnicas para explotar vulnerabilidades de inyección de comandos:

### 1. Uso de operadores de redirección

- **Entrada maliciosa**: `127.0.0.1 > output.txt`
- **Resultado esperado**: La salida del comando ping se redirigirá a un archivo, potencialmente sobrescribiendo archivos existentes.

### 2. Uso de backticks (en Linux)

- **Entrada maliciosa**: `127.0.0.1 \`whoami\``
- **Resultado esperado**: El comando entre backticks se ejecuta primero y su salida se usa como parte del comando principal.

### 3. Uso del operador pipe

- **Entrada maliciosa**: `127.0.0.1 | grep "bytes from"`
- **Resultado esperado**: La salida del comando ping se enviará al comando grep.

### 4. Uso de operadores lógicos

- **Entrada maliciosa**: `127.0.0.1 || ls -la`
- **Resultado esperado**: El comando `ls -la` se ejecutará si el comando ping falla.

## Diferencias en los resultados entre código vulnerable y seguro

Cuando se prueban las mismas entradas maliciosas en las versiones seguras de las clases, se observan las siguientes diferencias:

### SecureFileListingExample

- **Implementación segura**: Utiliza la API de Java (clase File) para listar archivos.
- **Resultado con entrada maliciosa**: No se ejecutará ningún comando adicional. Los caracteres como `;` o `&` se tratarán como parte del nombre de ruta.
- **Ejemplo**: Al introducir `/tmp; cat /etc/passwd`, simplemente intentará encontrar un directorio con ese nombre exacto (incluyendo el punto y coma), y fallará al no encontrarlo.

### SecureProcessBuilderExample

- **Implementación segura**: Separa el comando y sus argumentos en elementos distintos de una lista.
- **Resultado con entrada maliciosa**: Los caracteres especiales se tratan como parte del argumento, no como separadores de comandos.
- **Ejemplo**: Al introducir `/tmp; cat /etc/passwd`, el comando `ls` recibirá esto como un solo argumento, interpretándolo como una ruta.

### SecurePingExample

- **Implementación segura**: Valida la entrada con una expresión regular que solo permite direcciones IP válidas.
- **Resultado con entrada maliciosa**: Rechaza cualquier entrada que no cumpla con el patrón de una dirección IP válida.
- **Ejemplo**: Al introducir `127.0.0.1 && whoami`, mostrará un mensaje de error indicando un formato de IP inválido.

## Consecuencias potenciales de la explotación en sistemas reales

En un entorno de producción, la explotación de estas vulnerabilidades podría permitir a un atacante:

### 1. Acceso a información confidencial
- Leer archivos sensibles como `/etc/passwd`, configuraciones, archivos de credenciales
- Acceder a logs y datos de aplicaciones
- Obtener información sobre la estructura del sistema

### 2. Modificación del sistema
- Crear, modificar o eliminar archivos críticos
- Alterar configuraciones del sistema
- Desactivar mecanismos de seguridad

### 3. Escalada de privilegios
- Si la aplicación se ejecuta con privilegios elevados, el atacante podría obtener esos mismos privilegios
- Aprovechamiento de vulnerabilidades locales para escalar privilegios desde el contexto de la aplicación

### 4. Establecimiento de persistencia
- Crear tareas programadas o servicios para mantener el acceso
- Instalar backdoors o malware
- Configurar cuentas de usuario ocultas

### 5. Movimiento lateral
- Utilizar el sistema comprometido como punto de partida para atacar otros sistemas
- Escanear la red interna desde el sistema comprometido
- Robar credenciales para acceder a otros servicios

## Conclusión

Las vulnerabilidades de inyección de comandos representan un riesgo crítico para la seguridad de las aplicaciones. Como se ha demostrado en estos ejemplos, incluso funcionalidades aparentemente inofensivas como listar archivos o hacer ping a una dirección IP pueden convertirse en vectores de ataque si no se implementan con las medidas de seguridad adecuadas.

Las principales lecciones a tener en cuenta son:
1. Nunca concatenar directamente entrada de usuario en comandos del sistema
2. Evitar el uso de shells del sistema cuando sea posible
3. Usar APIs nativas del lenguaje en lugar de comandos del sistema
4. Validar rigurosamente cualquier entrada que deba usarse en un comando
5. Implementar el principio de mínimo privilegio para limitar el impacto de posibles ataques

Siguiendo estas prácticas, se pueden desarrollar aplicaciones más seguras y resistentes a ataques de inyección de comandos.
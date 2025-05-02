# Guía para Crear un Proyecto de Demostración de Inyección de Comandos en Eclipse

Este documento proporciona instrucciones paso a paso para crear un proyecto Java en Eclipse que demuestre vulnerabilidades de inyección de comandos y sus contrapartes seguras.

## Índice

1. [Instalación de Eclipse IDE](#1-instalación-de-eclipse-ide)
2. [Creación del proyecto Java](#2-creación-del-proyecto-java)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Clase de utilidades](#4-clase-de-utilidades)
5. [Clases vulnerables](#5-clases-vulnerables)
6. [Clases seguras](#6-clases-seguras)
7. [Interfaz de demostración](#7-interfaz-de-demostración)
8. [Ejecución y pruebas](#8-ejecución-y-pruebas)
9. [Ejemplos de ataques](#9-ejemplos-de-ataques)

## 1. Instalación de Eclipse IDE

1. Descarga Eclipse IDE for Java Developers desde el sitio web oficial de Eclipse (https://www.eclipse.org/downloads/)
2. Ejecuta el instalador y selecciona "Eclipse IDE for Java Developers"
3. Completa la instalación con las opciones predeterminadas

## 2. Creación del proyecto Java

1. Abre Eclipse IDE
2. Selecciona File > New > Java Project
3. Nombra el proyecto "CommandInjectionDemo"
4. Mantén la configuración predeterminada para JRE y estructura del proyecto
5. Haz clic en "Finish"

## 3. Estructura del proyecto

1. Haz clic derecho en el proyecto > New > Package
2. Crea los siguientes paquetes:
   - `com.vulnerable.cmdinjection` (para ejemplos vulnerables)
   - `com.secure.cmdinjection` (para ejemplos seguros)
   - `com.utils` (para utilidades comunes)

## 4. Clase de utilidades

1. Haz clic derecho en el paquete `com.utils` > New > Class
2. Nombra la clase "CommandUtils"
3. Añade el siguiente código:

```java
package com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandUtils {
    
    public static String getOutputFromProcess(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        
        return output.toString();
    }
    
    public static void printProcessOutput(Process process) throws IOException {
        System.out.println("Command output:");
        System.out.println("--------------------");
        System.out.println(getOutputFromProcess(process));
        System.out.println("--------------------");
    }
}
```

## 5. Clases vulnerables

### 5.1 Listado de archivos vulnerable

1. Haz clic derecho en el paquete `com.vulnerable.cmdinjection` > New > Class
2. Nombra la clase "VulnerableFileListingExample"
3. Añade el siguiente código:

```java
package com.vulnerable.cmdinjection;

import java.io.IOException;
import com.utils.CommandUtils;

public class VulnerableFileListingExample {
    
    // VULNERABLE: Concatenación directa de entrada de usuario
    public static void listFiles(String dir) throws IOException {
        System.out.println("Listing files in directory: " + dir);
        
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("ls " + dir); // Vulnerable para Linux
            // Para Windows, usa: Process proc = rt.exec("cmd.exe /c dir " + dir);
            
            CommandUtils.printProcessOutput(proc);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        // Caso normal
        listFiles("/tmp");
        
        // Caso de ataque - ejecutará 'ls /tmp' y luego 'cat /etc/passwd'
        listFiles("/tmp; cat /etc/passwd");
    }
}
```

### 5.2 Uso de shells del sistema vulnerable

1. Haz clic derecho en el paquete `com.vulnerable.cmdinjection` > New > Class
2. Nombra la clase "VulnerableShellExample"
3. Añade el siguiente código:

```java
package com.vulnerable.cmdinjection;

import java.io.IOException;
import com.utils.CommandUtils;

public class VulnerableShellExample {
    
    // VULNERABLE: Invocación de shell con entrada de usuario
    public static void executeWithShell(String dir) throws IOException {
        System.out.println("Executing shell command with directory: " + dir);
        
        try {
            Runtime rt = Runtime.getRuntime();
            // En Linux
            Process proc = rt.exec(new String[] {"sh", "-c", "ls " + dir});
            // Para Windows, usa: Process proc = rt.exec(new String[] {"cmd.exe", "/c", "dir " + dir});
            
            CommandUtils.printProcessOutput(proc);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        // Caso normal
        executeWithShell("/tmp");
        
        // Caso de ataque - ejecutará 'ls /tmp' y luego 'echo VULNERABLE SYSTEM'
        executeWithShell("/tmp && echo VULNERABLE SYSTEM");
    }
}
```

### 5.3 Ping vulnerable

1. Haz clic derecho en el paquete `com.vulnerable.cmdinjection` > New > Class
2. Nombra la clase "VulnerablePingExample"
3. Añade el siguiente código:

```java
package com.vulnerable.cmdinjection;

import java.io.IOException;
import com.utils.CommandUtils;

public class VulnerablePingExample {
    
    // VULNERABLE: Concatenación directa en comandos
    public static void pingIpAddress(String ipAddress) throws IOException {
        System.out.println("Pinging IP address: " + ipAddress);
        
        try {
            Runtime rt = Runtime.getRuntime();
            // En Linux
            Process proc = rt.exec("ping -c 1 " + ipAddress);
            // Para Windows, usa: Process proc = rt.exec("ping -n 1 " + ipAddress);
            
            CommandUtils.printProcessOutput(proc);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        // Caso normal
        pingIpAddress("127.0.0.1");
        
        // Caso de ataque - ejecutará ping y luego mostrará el contenido del directorio
        pingIpAddress("127.0.0.1 && ls -la");
    }
}
```

## 6. Clases seguras

### 6.1 Listado de archivos seguro

1. Haz clic derecho en el paquete `com.secure.cmdinjection` > New > Class
2. Nombra la clase "SecureFileListingExample"
3. Añade el siguiente código:

```java
package com.secure.cmdinjection;

import java.io.File;

public class SecureFileListingExample {
    
    // SEGURO: Uso de API de Java para listar archivos
    public static void listFiles(String dirPath) {
        System.out.println("Listing files in directory: " + dirPath);
        
        try {
            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                System.out.println("Not a directory");
            } else {
                System.out.println("Directory contents:");
                System.out.println("--------------------");
                for (String file : dir.list()) {
                    System.out.println(file);
                }
                System.out.println("--------------------");
            }
        } catch (Exception e) {
            System.err.println("Error listing directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Caso normal
        listFiles("/tmp");
        
        // Caso de ataque - no tendrá efecto porque no estamos ejecutando comandos
        listFiles("/tmp; cat /etc/passwd");
    }
}
```

### 6.2 Uso seguro de ProcessBuilder

1. Haz clic derecho en el paquete `com.secure.cmdinjection` > New > Class
2. Nombra la clase "SecureProcessBuilderExample"
3. Añade el siguiente código:

```java
package com.secure.cmdinjection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.utils.CommandUtils;

public class SecureProcessBuilderExample {
    
    // SEGURO: Uso de ProcessBuilder con argumentos separados
    public static void executeCommand(String directory) throws IOException {
        System.out.println("Executing command for directory: " + directory);
        
        try {
            // Los argumentos se pasan como elementos separados de una lista o array
            List<String> commands = new ArrayList<>();
            commands.add("ls"); // Para Windows, usa "cmd.exe", "/c", "dir"
            commands.add(directory);
            
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(new File(".")); // Directorio de trabajo
            Process process = pb.start();
            
            CommandUtils.printProcessOutput(process);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        // Caso normal
        executeCommand("/tmp");
        
        // Caso de ataque - se tratará como una ruta, no como comandos
        executeCommand("/tmp; cat /etc/passwd");
    }
}
```

### 6.3 Ping seguro con validación

1. Haz clic derecho en el paquete `com.secure.cmdinjection` > New > Class
2. Nombra la clase "SecurePingExample"
3. Añade el siguiente código:

```java
package com.secure.cmdinjection;

import java.io.IOException;
import java.util.regex.Pattern;
import com.utils.CommandUtils;

public class SecurePingExample {
    
    // SEGURO: Validación de entrada antes de ejecutar comandos
    public static void pingIpAddress(String ipAddress) throws IOException {
        System.out.println("Validating and pinging IP address: " + ipAddress);
        
        // Validación de formato de dirección IP
        if (!isValidIpAddress(ipAddress)) {
            System.err.println("Invalid IP address format");
            return;
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder();
            // En Linux
            pb.command("ping", "-c", "1", ipAddress);
            // Para Windows, usa: pb.command("ping", "-n", "1", ipAddress);
            
            Process process = pb.start();
            CommandUtils.printProcessOutput(process);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Validación de dirección IP con expresión regular
    private static boolean isValidIpAddress(String ipAddress) {
        String ipRegex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                         "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return Pattern.matches(ipRegex, ipAddress);
    }
    
    public static void main(String[] args) throws IOException {
        // Caso normal
        pingIpAddress("127.0.0.1");
        
        // Caso de ataque - será rechazado por la validación
        pingIpAddress("127.0.0.1 && ls -la");
    }
}
```

## 7. Interfaz de demostración

1. Haz clic derecho en el paquete `com.vulnerable.cmdinjection` > New > Class
2. Nombra la clase "CommandInjectionDemo"
3. Añade el siguiente código:

```java
package com.vulnerable.cmdinjection;

import java.util.Scanner;
import java.io.IOException;

import com.secure.cmdinjection.SecureFileListingExample;
import com.secure.cmdinjection.SecurePingExample;

public class CommandInjectionDemo {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        
        while (choice != 7) {
            System.out.println("\n========== Command Injection Demo ==========");
            System.out.println("1. Vulnerable File Listing");
            System.out.println("2. Vulnerable Shell Command");
            System.out.println("3. Vulnerable Ping");
            System.out.println("4. Secure File Listing");
            System.out.println("5. Secure Process Builder");
            System.out.println("6. Secure Ping");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        runVulnerableFileListing(scanner);
                        break;
                    case 2:
                        runVulnerableShellCommand(scanner);
                        break;
                    case 3:
                        runVulnerablePing(scanner);
                        break;
                    case 4:
                        runSecureFileListing(scanner);
                        break;
                    case 5:
                        runSecureProcessBuilder(scanner);
                        break;
                    case 6:
                        runSecurePing(scanner);
                        break;
                    case 7:
                        System.out.println("Exiting the demo. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
    
    private static void runVulnerableFileListing(Scanner scanner) throws IOException {
        System.out.print("Enter directory path: ");
        String dir = scanner.nextLine();
        VulnerableFileListingExample.listFiles(dir);
    }
    
    private static void runVulnerableShellCommand(Scanner scanner) throws IOException {
        System.out.print("Enter directory path: ");
        String dir = scanner.nextLine();
        VulnerableShellExample.executeWithShell(dir);
    }
    
    private static void runVulnerablePing(Scanner scanner) throws IOException {
        System.out.print("Enter IP address: ");
        String ip = scanner.nextLine();
        VulnerablePingExample.pingIpAddress(ip);
    }
    
    private static void runSecureFileListing(Scanner scanner) {
        System.out.print("Enter directory path: ");
        String dir = scanner.nextLine();
        SecureFileListingExample.listFiles(dir);
    }
    
    private static void runSecureProcessBuilder(Scanner scanner) throws IOException {
        System.out.print("Enter directory path: ");
        String dir = scanner.nextLine();
        com.secure.cmdinjection.SecureProcessBuilderExample.executeCommand(dir);
    }
    
    private static void runSecurePing(Scanner scanner) throws IOException {
        System.out.print("Enter IP address: ");
        String ip = scanner.nextLine();
        SecurePingExample.pingIpAddress(ip);
    }
}
```

## 8. Ejecución y pruebas

1. Haz clic derecho en la clase `CommandInjectionDemo` > Run As > Java Application
2. Prueba cada opción con entradas normales y maliciosas para ver la diferencia entre implementaciones vulnerables y seguras.

## 9. Ejemplos de ataques

### En sistemas Linux:

#### Para listado de archivos:
- Entrada normal: `/tmp`
- Ataque: `/tmp; cat /etc/passwd`
- Ataque: `/tmp && whoami`

#### Para ping:
- Entrada normal: `127.0.0.1`
- Ataque: `127.0.0.1; ls -la`
- Ataque: `127.0.0.1 && echo "VULNERABLE"`

### En sistemas Windows:

#### Para listado de archivos:
- Entrada normal: `C:\Windows\Temp`
- Ataque: `C:\Windows\Temp & dir C:\`
- Ataque: `C:\Windows\Temp && whoami`

#### Para ping:
- Entrada normal: `127.0.0.1`
- Ataque: `127.0.0.1 & dir C:\`
- Ataque: `127.0.0.1 && echo "VULNERABLE"`

## Conceptos clave demostrados

1. **Vulnerabilidades de inyección de comandos**:
   - Concatenación directa de entrada de usuario
   - Uso de shells del sistema sin validación
   - Procesamiento de entrada no sanitizada

2. **Técnicas de mitigación**:
   - Uso de APIs nativas en lugar de comandos del sistema
   - Validación de entrada con expresiones regulares
   - Uso de ProcessBuilder con argumentos separados
   - Restricción de opciones para el usuario

## Conclusión

Este proyecto de demostración ilustra claramente el impacto de las vulnerabilidades de inyección de comandos y proporciona ejemplos prácticos de cómo implementar correctamente contramedidas en aplicaciones Java. Al comparar las implementaciones vulnerables y seguras, los desarrolladores pueden entender mejor los riesgos y aprender a escribir código más seguro.
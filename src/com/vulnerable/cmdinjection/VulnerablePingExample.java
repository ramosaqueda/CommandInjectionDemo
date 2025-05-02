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
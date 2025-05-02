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
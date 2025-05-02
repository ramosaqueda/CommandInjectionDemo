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
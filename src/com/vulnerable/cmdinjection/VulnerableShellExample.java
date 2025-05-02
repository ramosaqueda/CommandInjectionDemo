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
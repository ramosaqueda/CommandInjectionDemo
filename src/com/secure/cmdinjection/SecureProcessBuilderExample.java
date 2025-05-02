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
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
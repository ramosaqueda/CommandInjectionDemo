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
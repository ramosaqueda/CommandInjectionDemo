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
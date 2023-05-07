/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isdcm.xml.encryption;

import java.util.Scanner;

/**
 *
 * @author david
 */
public class Main {   
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        while (true){
            XmlEncryption xmlEncryption = new XmlEncryption();
            System.out.println("Enter 0 for encrypting a file and 1 for decrypting");
            try{
                String mode = scanner.nextLine();
                String file;
                switch (mode){
                    case "0":
                        System.out.println("Enter file: ");
                        file = scanner.nextLine();
                        xmlEncryption.encryptXML(file);
                        break;
                    case "1":
                        System.out.println("Enter file: ");
                        file = scanner.nextLine();
                        xmlEncryption.decryptXML(file);
                        break;
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }            
        }
    }
}

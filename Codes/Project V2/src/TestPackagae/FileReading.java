/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestPackagae;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Prajanya
 */
public class FileReading {

    public static void main(String[] args) throws IOException {
        byte[] msgB = readFile("C:\\Users\\Prajanya\\Downloads\\Amnety.jpg");
        
        System.out.println(msgB);
    }
    //To read a file into a byte array using FileInputStream class in Java:
    public static byte[] readFile(String file) throws IOException {
        File f = new File(file);
        // work only for 2GB file, because array index can only up to Integer.MAX
        byte[] buffer = new byte[(int) f.length()];
        FileInputStream is = new FileInputStream(file);
        is.read(buffer);
        is.close();
        return buffer;
    }
     // To read file content into the string using BufferedReader and FileReader
    private static String fileReaderString(File filePath)
    {
        // Declaring object of StringBuilder class
        StringBuilder builder = new StringBuilder();
        // try block to check for exceptions where object of BufferedReader class us created to read filepath
        try (BufferedReader buffer = new BufferedReader(
                 new FileReader(filePath))) {
            String str;
            // Condition check via buffer.readLine() method  holding true upto that the while loop runs
            while ((str = buffer.readLine()) != null) {
                builder.append(str).append("\n");
            }
        }
        // Catch block to handle the exceptions
        catch (IOException e) {
            // Print the line number here exception occured using printStackTrace() method
            e.printStackTrace();
        }
        // Returning a string
        return builder.toString();
    }
//            BufferedReader reader = new BufferedReader(new FileReader(send));
//            StringBuilder stringBuilder = new StringBuilder();
//            char[] buffer = new char[10];
//            while (reader.read(buffer) != -1) {
//                    stringBuilder.append(new String(buffer));
//                    buffer = new char[10];
//            }
//            reader.close();
//            String fileContent = stringBuilder.toString();
}

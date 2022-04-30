/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.v2;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

public class Client {

    private static final String absolutePath = "C:\\Users\\Prajanya\\Documents\\Sender\\";
    private static String signFile = absolutePath + "signature.txt";
    private static String pubkeyEFile = absolutePath + "publickeyE.txt";
    private static String pubkeyNFile = absolutePath + "publickeyN.txt";

    public static void main(String[] args) {

        final File[] fileToSend = new File[1];

        JFrame jFrame = new JFrame("deep's client");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle = new JLabel("deep's File Sender");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Actual File Name is jlFileName
        JLabel jlFileName = new JLabel("choose a file to send");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        //jpButton JPanel hold jbSendFile & jbChooseFile Buttons
        JPanel jpButton = new JPanel(new GridLayout(0, 1, 0, 30));
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));

        JButton jbGenerate = new JButton("2. Generate Signature");
        jbGenerate.setPreferredSize(new Dimension(150, 75));
        jbGenerate.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbSendFile = new JButton("3. Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("1. Choose File");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jpButton.add(jbChooseFile);
        jpButton.add(jbGenerate);
        jpButton.add(jbSendFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JFileChooser provides an easy way for user to choose a file. Dialog box.
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");

                //null means it will appear on center of screen. APPROVIE_OPTION return a value if a file is chosen
                if (jFileChooser.showOpenDialog(null) == jFileChooser.APPROVE_OPTION) {
                    //When user selects the file, it will return to fileToSend
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    jlFileName.setText("The file you want to send is : " + fileToSend[0].getName());
                }

            }
        });
        jbGenerate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateDigitalSingature(fileToSend[0]);
            }
        });
        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileToSend[0] == null) {
                    jlFileName.setText("Please choose a file first");
                } else {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 1234);

                        //outputting from client to server
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        //We'll be sending the actual file name.
                        String fileName = fileToSend[0].getName();
                        //Turning above string  into Array of bytes
                        byte[] fileNameBytes = fileName.getBytes();

                        //Actual conent of file. It will be filled with byte value of content.
                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        // Part : Sending data to Server
                        //You first send an integer to the server which is the length of the data that it will be receiving. 
                        //The server then knows when it should stop expecting data from the client
                        //How much data will get sent based on fileName  & then acutal fileName.
                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);

                        //How much is byte array of fileContent and after server knows that we will send the acutal fileContent.
                        dataOutputStream.writeInt(fileContentBytes.length);
                        dataOutputStream.write(fileContentBytes);

                    } catch (IOException error) {
                        error.printStackTrace();
                    }
                }
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);

    }

    public static void generateDigitalSingature(File send) {
        try {
            /* Generate a key pair */
            //Step 4 in the signing process and step 1, 2 and 4 in the verification process is not 
            //necessary, but I am only going through them for simulation purposes.
            //Signing process:

            //1. Bob generates a message (user input) : The message is text file.       
            BufferedReader reader = new BufferedReader(new FileReader(send));
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[10];
            while (reader.read(buffer) != -1) {
                stringBuilder.append(new String(buffer));
                buffer = new char[10];
            }
            reader.close();
            String fileContent = stringBuilder.toString();

            //2. Bob Generates hash value for the message (SHA-256 with arbitrary length input)
            SHA256 sha256 = new SHA256();
            byte[] msgBytes = readFile(send);    
            System.out.println("The number of characters is "+msgBytes.length);
            byte[] bobsHash = sha256.hash(msgBytes);
            String bobsHashStr = DatatypeConverter.printHexBinary(bobsHash);//variable for printing
//            String bobsHashStr = sha256.getSHA256Hash(fileContent);
            System.out.println("Bob's hash in String: " + bobsHashStr);

            //3. Bob uses the hash value together with a private key as input for RSA algorithm
            RSA rsa = new RSA();
            BigInteger[] pk = getPK(rsa);
            BigInteger[] puk = getPUK(rsa);
            System.out.println("Encrypting Bob's hash: " + bobsHashStr);
            System.out.println("Bob's hash in Bytes: " + rsa.toString(bobsHashStr.getBytes()));

//        3.3 Displaying KEYS
            System.out.println("Bobs private key is " + Arrays.toString(pk));
            System.out.println("Bobs public key is " + Arrays.toString(puk));

            //Encrypt/Generate digital signature
            byte[] encryptedSignature = rsa.encrypt(pk[0], pk[1], bobsHashStr.getBytes());
            /*
        Encrypted without the private key of Bob.
        byte[] encrypted = rsa.encrypt(BigInteger.ONE,BigInteger.TEN,bobsHashStr.getBytes());
             */
            System.out.println("Bob's encrypted Hash i.e. Digital Signature: " + DatatypeConverter.printHexBinary(encryptedSignature));

            //4. Bob sends message with RSA signature attached.
            /* Save the signature in a file */
            FileOutputStream sigfos = new FileOutputStream(signFile);
            sigfos.write(encryptedSignature);
            sigfos.close();

            /* Save the public key (e,n) in a file */
            writeBigIntegerToFile(pubkeyEFile, puk[0]);
            writeBigIntegerToFile(pubkeyNFile, puk[1]);

        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }

    public static void writeBigIntegerToFile(String sFile, BigInteger Big) {
        try {

            // create a new file with an ObjectOutputStream
            FileOutputStream out = new FileOutputStream(sFile);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            // write the number into a new file
            oout.writeObject(Big);
            // close the stream
            oout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BigInteger[] getPK(RSA rsa) {
        BigInteger[] pk = rsa.getPrivateKey();//Bob's private key
        return pk;
    }

    public static BigInteger[] getPUK(RSA rsa) {
        BigInteger[] puk = rsa.getPublicKey();//Bob's public key
        return puk;

    }

    //To read a file into a byte array using FileInputStream class in Java:
    public static byte[] readFile(File file) throws IOException {
        // work only for 2GB file, because array index can only up to Integer.MAX
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream is = new FileInputStream(file);
        is.read(buffer);
        is.close();
        return buffer;
    }

}

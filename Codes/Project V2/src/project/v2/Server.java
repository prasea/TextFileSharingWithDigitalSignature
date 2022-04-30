/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.v2;

/**
 *
 * @author Prajanya
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.bind.DatatypeConverter;

public class Server {

    private static final String absolutePath = "C:\\Users\\Prajanya\\Documents\\Receiver\\";
//    public static String receivedFile = absolutePath+"Hello.txt";
    private static String signFile = absolutePath + "signature.txt";
    private static String pubkeyEFile = absolutePath + "publickeyE.txt";
    private static String pubkeyNFile = absolutePath + "publickeyN.txt";

    public static ArrayList<String> filesToReceive = new ArrayList<String>();

    //Static global variable myFiles is DataStrucutre to hold each of the file that client sends
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        //We increment file ID after each file is sent by user.
        int fileId = 0;

        JFrame jFrame = new JFrame("Deep's Server");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS)); //To stack everything Vertically.
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("Deep's File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlDesc = new JLabel("Note : Click on individual file to download it. Then click Verify Button.");
        jlDesc.setFont(new Font("Arial", Font.BOLD, 10));
        jlDesc.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbVerify = new JButton("Verify");
        jbVerify.setPreferredSize(new Dimension(100, 75));
        jbVerify.setFont(new Font("Arial", Font.BOLD, 20));
        jbVerify.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.add(jlDesc);
        jFrame.add(jbVerify);
//        jFrame.add(Box.createVerticalStrut(20));
        jFrame.setVisible(true);

        jbVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isVerified = verifyDigitalSignature(new File(absolutePath + filesToReceive.get(0)));
//                  boolean isVerified =  verifyDigitalSignature(new File(receivedFile));

                if (isVerified) {
                    jlTitle.setText("The file is not tempered");
                } else {
                    jlTitle.setText("The file is tempered");
                }
                jPanel.removeAll();
                jPanel.revalidate();
                jPanel.repaint();
            }
        });
        /*
            A server socket waits for requests to come in over the network, performs some operation based on that request, 
            and can returns a result to the requester
         */
        ServerSocket serverSocket = new ServerSocket(1234);

        //Our Server app will be constantly running, so we can send multiple files.. So we used while(true) which runs forever unless you break
        while (true) {
            try {
                //Server will be waiting for client to JOIN and when they do, it will return socket object that you can communicate with them over
                Socket socket = serverSocket.accept();

                //To get incoming data from client
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                int fileNameLength = dataInputStream.readInt();

                //If file has been sent
                if (fileNameLength > 0) {
                    //Creating byte array of size fileNameLength.
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    //Converting array of bytes of fileName to String.
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                        //To hold the list of received file row 
                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            filesToReceive.add(jlFileName.getText());
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        } else {

                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);

                            jFrame.validate();
                        }

                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));

                        fileId++;

                    }

                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        }

    }

    public static MouseListener getMyMouseListener() {

        return new MouseListener() {
            //when we click on jPanel we're returning is name which is File ID that we set in jpFileRow
            @Override
            public void mouseClicked(MouseEvent e) {

                JPanel jPanel = (JPanel) e.getSource();
                //We set the File Name to be FileId while checking for "txt"
                int fileId = Integer.parseInt(jPanel.getName());

                for (MyFile myFile : myFiles) {

                    if (myFile.getId() == fileId) {
                        //jfPreview is pop out  that has info about file that we clicked on ! 
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    //fileName, fileData, fileExtension are used to populate JFrame that appears when we click on file that has been sent.
    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        JFrame jFrame = new JFrame("Deep's File Downloader");
        jFrame.setSize(800, 400);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JLabel jlTitle = new JLabel("Deep's File Downloader");
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel jlPrompt = new JLabel("Are you sure you want to download file " + fileName);
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));

        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel jlFileContent = new JLabel();
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

//        if(fileExtension.equalsIgnoreCase("txt")){
//            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
//            //Using <html> will insert line-breaks otherwise there won't be word-wrap
//        } else{
//            //If NOT text file, it will display an Image 
//            jlFileContent.setIcon(new ImageIcon(fileData));
//        }
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//Creating this File object is what downloads/creates the file. The file will not have any content though until we write thre data that user sent into it.
//fileName is path where we wanna download the file.
                File fileToDownload = new File(absolutePath + fileName);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);

                    fileOutputStream.write(fileData);
                    fileOutputStream.close();

                    jFrame.dispose();

                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        });

        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        jFrame.add(jPanel);

        return jFrame;
    }
//The method must be static because we are using it in the main method which is static

    public static String getFileExtension(String fileName) {

        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
        }
    }

    public static boolean verifyDigitalSignature(File receive) {
        try {

            BigInteger[] puk = new BigInteger[2];
            puk[0] = readFile(pubkeyEFile);
            puk[1] = readFile(pubkeyNFile);

            /*verification: 1. Alice receives the message with signature.*/
            BufferedReader reader = new BufferedReader(new FileReader(receive));
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[10];
            while (reader.read(buffer) != -1) {
                stringBuilder.append(new String(buffer));
                buffer = new char[10];
            }
            reader.close();
            String fileContent = stringBuilder.toString();

            /*2. Alice calculates a hash value for the received message, just as Bob did in step 2 of
        the signing process (SHA-256 with arbitrary length input).*/
            SHA256 sha256 = new SHA256();
            byte[] rMsgBytes = readFile(receive);
            byte[] rHash = sha256.hash(rMsgBytes);
            String rHashStr = DatatypeConverter.printHexBinary(rHash);//variable for printing
//            String rHashStr = sha256.getSHA256Hash(fileContent);
            System.out.println("Alices' hash in String: " + rHashStr);

            /*3. Alice provides the received encryption and Bob's public key as input for RSA  decryption.*/
            Path path = Paths.get(signFile);
            byte[] encryptedSignature = Files.readAllBytes(path);
            RSA rsa = new RSA();
            byte[] decrypted = rsa.decrypt(puk[0], puk[1], encryptedSignature);
            System.out.println("Alices' decrypted Hash in bytes: " + rsa.toString(decrypted));
            System.out.println("Alices' decrypted Hash in String: " + new String(decrypted));

            /*4. Alice compares the RSA decryption with her calculated hash value for the received
        message. If the algoritm returns the result that the signature is valid (which in this 
        case it is, because Alice's decrypted hash matches Bob's original hash), Alice is assured 
        that the message has been signed by Bob, because nobody else has his private key, so 
        nobody else could have created a signature for this message that could be verified for 
        this message using Bob's key.*/
            if (new String(decrypted).equals(rHashStr)) {
                System.out.println("Verification successfull! Alices' decryption matches her "
                        + "calculated hash -> The message is signed by Bob!");
                return true;
            } else {
                System.out.println("Verification successful! Alice's decryption does not match her "
                        + "calculated hash -> The message is NOT signed by Bob!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
        return false;
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

    public static BigInteger readFile(String rFile) {
        BigInteger Big = null;
        try {

            // create an ObjectInputStream for the new file
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rFile));

            // convert new file into a BigInteger
            Big = (BigInteger) ois.readObject();
            System.out.println("The BigInteger is " + Big);
            return Big;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Big;
    }
}

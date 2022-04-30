/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sha256rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import javax.xml.bind.DatatypeConverter;

import javax.xml.bind.DatatypeConverter;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        //Step 4 in the signing process and step 1, 2 and 4 in the verification process is not 
        //necessary, but I am only going through them for simulation purposes.
        //Signing process:
        //1. Bob generates a message (user input)
        @SuppressWarnings("resource")
        Scanner inn = new Scanner(System.in);
        String message = inn.nextLine();
		
        //2. Bob Generates hash value for the message (SHA-256 with arbitrary length input)
        SHA256 sha256 = new SHA256();
        byte[] msgBytes = message.getBytes();
        byte[] bobsHash = sha256.hash(msgBytes);
        String bobsHashStr = DatatypeConverter.printHexBinary(bobsHash);//variable for printing
        System.out.println("Bob's hash in String: " + bobsHashStr);
        
        
        //2.2 Using Library Hash        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));
        System.out.println("Library hash in String"+DatatypeConverter.printHexBinary(hash));
    
        
        //3. Bob uses the hash value together with a private key as input for RSA algorithm
        RSA rsa = new RSA();
        BigInteger[] pk = rsa.getPrivateKey();//Bob's private key
        BigInteger[] puk = rsa.getPublicKey();//Bob's public key
//        System.out.println("Bobs private key is "+ Arrays.toString(pk));
//        System.out.println("Bobs public key is "+Arrays.toString(puk));
        System.out.println("Encrypting Bob's hash: " + bobsHashStr);
        System.out.println("Bob's hash in Bytes: "
                + rsa.toString(bobsHashStr.getBytes()));
        
//        3.3 Displaying KEYS
    
        //Encrypt/Generate digital signature
        byte[] encrypted = rsa.encrypt(pk[0],pk[1],bobsHashStr.getBytes());
        /*
        Encrypted without the private key of Bob.
        byte[] encrypted = rsa.encrypt(BigInteger.ONE,BigInteger.TEN,bobsHashStr.getBytes());
        */
        System.out.println("Bob's encrypted Hash i.e. Digital Signature: " + DatatypeConverter.printHexBinary(encrypted));
        
		//4. Bob sends message with RSA signature attached.
		
		/*verification:
		1. Alice receives the message with signature.*/
        String receivedMessage = message;
        
		/*2. Alice calculates a hash value for the received message, just as Bob did in step 2 of
        the signing process (SHA-256 with arbitrary length input).*/
        byte[] rMsgBytes = receivedMessage.getBytes();
        byte[] rHash = sha256.hash(rMsgBytes);
        String rHashStr = DatatypeConverter.printHexBinary(rHash);//variable for printing
        System.out.println("Alices' hash in String: " + rHashStr);
        
		/*3. Alice provides the received encryption and Bob's public key as input for RSA 
        decryption.*/
        byte[] decrypted = rsa.decrypt(puk[0],puk[1],encrypted);
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
        } else {
        	System.out.println("Verification successful! Alice's decryption does not match her "
        			+ "calculated hash -> The message is NOT signed by Bob!");
        }
	}
     

}

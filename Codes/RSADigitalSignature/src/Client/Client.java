/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 *
 * @author Prajanya
 */
public class Client {
    //name for this client, otherParyUsername for other client.
    //n for this client, encryptN for other client. 
    //e is public key for this client, encrpytE is public key of other client. 
    //d is private key of this client
    private String name = null;
    private String otherPartyUsername = null;
    private BigInteger n = null;
    private BigInteger encryptN = null;
    private BigInteger phi = null;
    private BigInteger e = null;
    private BigInteger encryptE = null;
    private BigInteger d = null;
    private PrintWriter printWriter;
    public static void main(String[] args) throws IOException {
        //TODO Auto-generated method stub
        Client client = new Client();
        Socket socket = new Socket("localhost", 4444);
        //We instantiate & start ClientThread which triggers call to run() in ClientThread when OS decides
        new ClientThread(socket, client).start();
        
        client.printWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        StringWriter stringWriter = new StringWriter();
        RSADigitalSignatureHelper.handleGenerateKeys(bufferedReader, stringWriter, client);
        while(true)
        {
            //User entering & sending signed messages
            RSADigitalSignatureHelper.sendMessage(bufferedReader, client);
        }
    }
    public BigInteger getE(){
        return e;
    }
    public void setE(BigInteger e){
        this.e = e;
    }
    public PrintWriter getPrintWriter(){
        return printWriter;
    }
    public void setPrintWriter(PrintWriter printWriter){
        this.printWriter = printWriter;
    }
    public BigInteger getEncryptN(){
        return encryptN;
    }
    public BigInteger setEncryptN(BigInteger encryptN){
        this.encryptN = encryptN;
        return null;
    }
    public BigInteger getEncryptE(){
        return encryptE;
    }
    public BigInteger setEncryptE(BigInteger encryptE){
        this.encryptE = encryptE;
        return null;
    }    
    public BigInteger getD(){
        return d;
    }
    public void setD(BigInteger d){
        this.d = d;
    }
    public BigInteger getN(){
        return n;
    }
    public void setN(BigInteger n){
        this.n = n;
    }
    public BigInteger setPhi(BigInteger phi){
        this.phi = phi;
        return null;
    }
    public BigInteger getPhi(){
        return phi;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public String getOtherPartyUsername(){
        return otherPartyUsername;
    }
    public void setOtherParyUsername(String otherPartyUsername){
        this.otherPartyUsername = otherPartyUsername;
    }
}

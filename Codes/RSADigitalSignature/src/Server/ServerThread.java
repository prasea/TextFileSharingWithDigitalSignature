/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author Prajanya
 * uses socket to initialize bufferedReader & printWriter
 */
public class ServerThread extends Thread{
    private Server server;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    public ServerThread(Socket socket, Server server) throws IOException {
        this.server = server;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
    }
    void forwardMessage(String message){
        printWriter.println(message);
    }
//    JSON message from Server contains public key "e"
    public void run()
    {
        JsonObject jsonObject = null;
        try{
            while(true){
                jsonObject = Json.createReader(bufferedReader).readObject();
                System.out.println("[System] : "+ jsonObject.toString());
                if(jsonObject.containsKey("e")){
                    System.out.println("[Eve] : "+ jsonObject.getString("name")+"'s public key (n, e) - ("+ jsonObject.getString("n")+", "+jsonObject.getString(("e")+")."));
                    System.out.println("                Need private key d");
                    System.out.println("                where d*e <congurent> 1 mod phi(n)");
                    System.out.println("                 but don't have phi(n).");
                    System.out.println("                To obtain it can use formula : phi(n) = (p-1)(q-1)");
                    System.out.println("                where p*q = n & both numbers are primes");
                    System.out.println("                i.e. need to do prime factorization of n into p & q");
                }
                server.forwardMessage(jsonObject.toString(), this);                
            }
        } catch(Exception e){
            server.getServerThreads().remove(this);
        }
    }
    
}

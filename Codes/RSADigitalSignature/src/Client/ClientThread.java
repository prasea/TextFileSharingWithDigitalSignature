/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.json.JsonObject;
import javax.json.Json;

/**
 *
 * @author Prajanya
 */
public class ClientThread extends Thread{
    private BufferedReader reader;
    private Client client;
    public ClientThread(Socket socket, Client client) throws IOException
    {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.client = client;
    }
    public void run()
    {
        while(true)
        {
            JsonObject jsonObject = Json.createReader(reader).readObject();
            //If incoming json message contians public key, we call handleReceivePublicKey.  
            if(jsonObject.containsKey("e"))
            {
                RSADigitalSignatureHelper.handleReceivePublicKey(jsonObject, client);
            }
            //If it contains signed message, we call handleReceiveMessage
            else if(jsonObject.containsKey("x")){
                RSADigitalSignatureHelper.handleReceiveMessage(jsonObject, client);
            }
        }
    }
}

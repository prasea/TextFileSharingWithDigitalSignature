/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Prajanya
 * server will accept connection on PORT. 
 * It will have Set of ServerThread for different clients that this server is interacting with.
 * while Server is running, we accept connection from clients & instantiate ServerThread for those connection.
 * Add those ServerThread to Set<ServerThread>
 */
public class Server {
    static final int PORT = 4444;
    private ServerSocket serverSocket;
    private Set<ServerThread> serverThreads = new HashSet<ServerThread>();
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.serverSocket = new ServerSocket(PORT);
        System.out.println("[EVE] : eavesdropping on all communications");
        while(true){
            ServerThread serverThread = new ServerThread(server.serverSocket.accept(), server);
			server.serverThreads.add(serverThread);
            //Trigger run() in ServerThread
            serverThread.start();
        }
    }
    
    public Set<ServerThread> getServerThreads(){
        return serverThreads;
    }
    void forwardMessage(String message, ServerThread originatingT){
        serverThreads.stream().filter(t -> t != originatingT).forEach(t ->t.forwardMessage(message));
    }
}

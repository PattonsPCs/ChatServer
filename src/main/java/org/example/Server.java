package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    final private int port;
    final private List<Socket> synchronizedList;
    private Socket connection;
    private boolean loopFlag;
    // We already made the server socket, but the issue is that IO Exception is thrown.
    // We need to handle this exception.
    // We could use a try-catch, but let's try putting it in a method.
    public Server(){
        this.port = 12345;
        List<Socket> connectedClients = new ArrayList<>();
        this.synchronizedList = Collections.synchronizedList(connectedClients);
        this.loopFlag = true;
    }



    public void clientHandler() throws IOException{
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            System.out.println("Chat Server is starting...");
            System.out.println("Chat Server started on port: " + this.port);

            while(loopFlag){
                connection = serverSocket.accept();
                addClient(connection);
                System.out.println("Client: " + connection.getInetAddress().toString() + " has joined!");
                Thread clientThread = new Thread(() ->{
                    try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                        String message;
                        while((message = in.readLine()) != null){
                            if(message.equalsIgnoreCase("/quit")){
                                System.out.println("Client: " + connection.getInetAddress().toString() + " requested to close the connection...");
                                removeClient(connection);
                                loopFlag = false;
                                break;
                            }
                            System.out.println("Received: " + message + " from " + connection.getInetAddress().toString());
                            for(Socket client : synchronizedList){
                                if(!client.equals(connection)){
                                    broadcast(client, message);
                                }
                            }
                        }
                    }  catch (IOException e){
                        logger.error("Error: {}", e.getMessage(), e);
                    }
                });
                clientThread.start();
            }
        }
    }




    public void startServer(){
       try{
           clientHandler();
       } catch (IOException e) {
           logger.error("Error starting server: {}", e.getMessage(), e);
       }
    }

    public void addClient(Socket connection){
        synchronizedList.add(connection);
    }

    public void removeClient(Socket connection){
        synchronizedList.remove(connection);
    }

    public void broadcast(Socket client, String message) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
        out.println(client.getInetAddress().toString() + ": " + message);
    }

    public static void main(String[] args){
        Server server = new Server();
        server.startServer();
    }

}




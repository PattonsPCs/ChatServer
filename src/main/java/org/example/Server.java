package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;



public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    final private int port;
    private final AtomicInteger clientIdCounter;
    final private List<ClientHandler> synchronizedList;
    private final AtomicBoolean loopFlag;
    // We already made the server socket, but the issue is that IO Exception is thrown.
    // We need to handle this exception.
    // We could use a try-catch, but let's try putting it in a method.
    public Server(){
        this.port = 12345;
        List<ClientHandler> connectedClients = new ArrayList<>();
        this.synchronizedList = Collections.synchronizedList(connectedClients);
        this.loopFlag = new AtomicBoolean(true);
        this.clientIdCounter = new AtomicInteger(0);
    }



    public void clientHandler() throws IOException{
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            System.out.println("Chat Server is starting...");
            System.out.println("Chat Server started on port: " + this.port);

            while(loopFlag.get()){
                Socket connection = serverSocket.accept();
                int clientId = clientIdCounter.incrementAndGet();
                ClientHandler clientHandler = new ClientHandler(connection, clientId);
                addClient(clientHandler);
                System.out.println("Client " + clientId + " has joined!");
                Thread clientThread = new Thread(clientHandler);
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

    public void addClient(ClientHandler clientHandler){
        synchronizedList.add(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler){
        synchronizedList.remove(clientHandler);
    }

    public void broadcast(ClientHandler sender, String message) throws IOException {
        for(ClientHandler client : synchronizedList){
            if(!client.equals(sender)){
                client.sendMessage("Client " + sender.getClientId() + ": " + message);
            }
        }
    }


    public static void main(String[] args){
        Server server = new Server();
        server.startServer();
    }

    public class ClientHandler implements Runnable{
        private final Socket connection;
        private final int clientId;
        public ClientHandler(Socket connection, int clientId){
            this.connection = connection;
            this.clientId = clientId;
        }

        public int getClientId() {
            return clientId;
        }

        @Override
        public void run(){
            try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String message;
                while((message = in.readLine()) != null){
                    if(message.equalsIgnoreCase("/quit")){
                        System.out.println("Client " + clientId + " requested to close the connection...");
                        removeClient(this);
                        connection.close();
                        break;
                    }
                    System.out.println("Received " + message + " from Client " + clientId);
                    broadcast(this, message);
                }
            } catch (IOException e){
                logger.error("Error: {}", e.getMessage(), e);
            }
        }

        public void sendMessage(String message) throws IOException{
            PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()), true);
            out.println(message);
        }
    }

}




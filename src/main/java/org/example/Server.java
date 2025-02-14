package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    final private int port;
    private ArrayList<String> connectedClients;
    private ChatServer chatServer;
    // We already made the server socket, but the issue is that IO Exception is thrown.
    // We need to handle this exception.
    // We could use a try-catch, but let's try putting it in a method.
    public Server(){
        this.port = 12345;
        this.connectedClients = new ArrayList<>();
        this.chatServer = new ChatServer(this.port);
    }

    public void clientHandler() throws IOException{
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            System.out.println("Server is starting...");
            System.out.println("Server started on port: " + this.port);

            while(true){
                Socket connection = serverSocket.accept();
                chatServer.addClient(connection);
                System.out.println("Client: " + connection.getInetAddress().toString() + " has joined!");
                Thread clientThread = new Thread(() ->{
                    try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                        String message;
                        while((message = in.readLine()) != null){
                            System.out.println("Received: " + message);
                            chatServer.broadCast(message);
                        }
                    }  catch (IOException e){
                        System.out.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    } finally{
                        chatServer.removeClient(connection);
                    }
                });
                clientThread.start();
            }
        }
    }




    public void startServer() throws IOException{
        clientHandler();
    }

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.startServer();
    }

}




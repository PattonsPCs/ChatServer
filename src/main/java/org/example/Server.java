package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    final private int port;
    final private ArrayList<Socket> connectedClients;
    private Socket connection;
    // We already made the server socket, but the issue is that IO Exception is thrown.
    // We need to handle this exception.
    // We could use a try-catch, but let's try putting it in a method.
    public Server(){
        this.port = 12345;
        this.connectedClients = new ArrayList<>();
        this.connection = new Socket();
    }



    public void clientHandler() throws IOException{
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            System.out.println("Chat Server is starting...");
            System.out.println("Chat Server started on port: " + this.port);

            while(true){
                this.connection = serverSocket.accept();
                addClient(this.connection);
                System.out.println("Client: " + this.connection.getInetAddress().toString() + " has joined!");
                Thread clientThread = new Thread(() ->{
                    try(BufferedReader in = new BufferedReader(new InputStreamReader(this.connection.getInputStream()))){
                        String message;
                        while((message = in.readLine()) != null){
                            if(message.equalsIgnoreCase("/quit")){
                                System.out.println("Client: " + this.connection.getInetAddress().toString() + " requested to close the connection...");
                                removeClient(this.connection);
                                break;
                            }
                            System.out.println("Received: " + message + " from " + this.connection.getInetAddress().toString());
                            for(Socket client : connectedClients){
                                broadcast(client, message);
                            }
                        }
                    }  catch (IOException e){
                        System.out.println("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                clientThread.start();
            }
        }
    }




    public void startServer() throws IOException{
        clientHandler();
    }

    public void addClient(Socket connection){
        this.connectedClients.add(connection);
    }

    public void removeClient(Socket connection){
        this.connectedClients.remove(connection);
    }

    public void broadcast(Socket client, String message) throws IOException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
        out.println(client.getInetAddress().toString() + ": " + message);
    }

    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.startServer();
    }

}




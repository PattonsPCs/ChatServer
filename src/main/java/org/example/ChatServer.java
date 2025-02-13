package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer {
    final private int port;
    ArrayList<String> connectedClients;
    private Socket connection;
    // We already made the server socket, but the issue is that IO Exception is thrown.
    // We need to handle this exception.
    // We could use a try-catch, but let's try putting it in a method.
    public ChatServer(){
        this.port = 12345;
        this.connectedClients = new ArrayList<>();
    }

    public void clientHandler() throws IOException{
        ServerSocket serverSocket = new ServerSocket(this.port);
        System.out.println("Server is starting...");
        System.out.println("Server started on port: " + this.port);
        while(this.connectedClients != null){
            connection = serverSocket.accept();
            System.out.println("Client: " + connection.getInetAddress().toString() + " has joined!");
            Thread clientThread = new Thread(() ->{
                try{
                    receiveData(connection);
                }  catch (IOException e){
                    System.out.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            });
         clientThread.start();
        }
        connection.close();
    }

    public void receiveData(Socket clientConnection) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientConnection.getOutputStream()), true);
        while(true){
            String data = in.readLine();

            if(data == null){
                System.out.println("Client: " + clientConnection.getInetAddress().toString() + " closed the connection...");
                break;
            }else if(data.equalsIgnoreCase("/quit")){
                System.out.println("Client: " + clientConnection.getInetAddress().toString() + " requested to close the connection...");
                break;

            }
            System.out.println(clientConnection.getInetAddress().toString() + " said: " + data);
            out.println("Message Received!");
        }
    }



    public void startServer() throws IOException{
        clientHandler();
    }

    public static void main(String[] args) throws IOException{
        ChatServer server = new ChatServer();
        server.startServer();
    }

}




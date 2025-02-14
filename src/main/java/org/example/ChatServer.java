package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class ChatServer {
    private final int port;
    ArrayList<Socket> clients;
    public ChatServer(int port){
        this.port = port;
        clients = new ArrayList<>();
    }

    public void addClient(Socket client){
        clients.add(client);
    }

    public void removeClient(Socket client){
        clients.remove(client);
    }

    public void broadCast(String message) throws IOException{
        for(Socket client : clients){
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
            out.println(message);
        }

    }


    public static void main(String[] args){
        Server server = new Server();
        try{
            server.startServer();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}




package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final private String host;
    final private int port;
    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(){
        this.host = "localhost";
        this.port = 12345;
    }

    public void clientFunctionality() throws IOException {
        Socket clientSocket = new Socket(this.host, this.port);
        System.out.println("Client: " + clientSocket.getInetAddress().toString() + " starting up...");
        System.out.println("Connected to the server!");


        ChatRunnable chatRunnable = new ChatRunnable(clientSocket);
        Thread thread = new Thread(chatRunnable);
        thread.start();

        Scanner clientMessageInput = new Scanner(System.in);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

        while(clientSocket.isConnected()){
            System.out.println("Enter a message to send to the server: ");
            String clientMessage = clientMessageInput.nextLine();
            if(clientMessage.equalsIgnoreCase("/quit")){
                System.out.println("Shutting down...");
                clientSocket.close();
                break;
            }else if(clientMessage.isEmpty()){
                System.out.println("Bro ain't write anything \uD83D\uDC80 \uD83D\uDC80 \uD83D\uDC80");
                break;
            }else{
                out.println(clientMessage);
            }

        }
        clientSocket.close();
    }



    public void startClient(){
        try{
            clientFunctionality();
        } catch (IOException e){
            logger.error("Error starting client: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args){
        Client client = new Client();
        client.startClient();
    }
}

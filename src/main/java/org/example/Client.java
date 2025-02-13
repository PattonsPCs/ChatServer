package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    final private String host;
    final private int port;


    public Client(){
        this.host = "localhost";
        this.port = 12345;
    }

    public void clientFunctionality() throws IOException {
        Socket clientSocket = new Socket(this.host, this.port);
        System.out.println("Client: " + clientSocket.getInetAddress().toString() + " starting up...");
        System.out.println("Connected to the server!");
        Scanner clientMessageInput = new Scanner(System.in);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        while(clientSocket.isConnected()){

            System.out.println("Enter a message to send to the server: ");
            String clientMessage = clientMessageInput.nextLine();

            if(clientMessage.equalsIgnoreCase("/quit")){
                System.out.println("Shutting down...");
                clientSocket.close();
            }else if(clientMessage.isEmpty()){
                System.out.println("Client did not respond. Shutting down...");
                clientSocket.close();
            }else{
                out.println(clientMessage);
            }

            String response = in.readLine();
            System.out.println("Server said: " + response);

        }
        clientSocket.close();
    }



    public void startClient() throws IOException{
        clientFunctionality();
    }

    public static void main(String[] args) throws IOException{
        Client client = new Client();
        client.startClient();
    }
}

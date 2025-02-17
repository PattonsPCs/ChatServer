package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatRunnable implements Runnable {

    final private Socket connectionSocket;

    public ChatRunnable(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
            String message;
            while((message = in.readLine()) != null){
                System.out.println(message);

            }        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

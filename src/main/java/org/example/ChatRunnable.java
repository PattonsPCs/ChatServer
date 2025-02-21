package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class ChatRunnable implements Runnable {

    final private Socket connectionSocket;
    private static final Logger logger = LoggerFactory.getLogger(ChatRunnable.class);

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

            }
        }catch (SocketException e){
            System.out.println("Cya nerd\uD83D\uDC80");
        } catch (IOException e) {
            logger.error("Error reading message: {}", e.getMessage(), e);
        }
    }

}

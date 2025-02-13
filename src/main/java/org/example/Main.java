package org.example;
import org.example.ChatServer;
import org.example.Client;

import java.io.IOException;

public class Main{

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        Client client = new Client();

        server.startServer();
        client.startClient();
    }

}
package client;

import java.io.IOException;
import java.net.Socket;

public class ClienteMain{
    
    private static String host = "localhost";
    private static int port = 4030;
    private static Socket sc;

    public static void main(String[] args) {
        try {
            sc = new Socket(host, port);
            System.out.println("Client: connected to server");

            
            ClientThread c = new ClientThread(sc,1);
            c.start(); 
            
        } catch (IOException e) {
            System.out.println("Client: connection to server - ERROR");
        }
    }
}

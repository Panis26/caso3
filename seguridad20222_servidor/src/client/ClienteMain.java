package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClienteMain{
    
    private static String host = "localhost";
    private static int port = 4030;

    public static void main(String[] args) {
        Scanner ss = new Scanner(System.in);
        System.out.println("Ingrese la canitdad de delegados concurrentes: ");
        int cantDelegados = ss.nextInt();
        
        try {
            for (int i = 0; i <cantDelegados ; i++) {
                Socket sc = new Socket(host, port);
                System.out.println("Client " + i + ": connecting to server - done");
                ClientThread c = new ClientThread(sc, i);
                c.start();
            }
            
        } catch (IOException e) {
            System.out.println("Client: connection to server - ERROR");
        }
    }
}

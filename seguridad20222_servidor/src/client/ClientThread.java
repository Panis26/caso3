package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Security;

public class ClientThread extends Thread{
    private Socket sc;
    private int id;


    public ClientThread(Socket sc, int id) {
        this.sc = sc;
        this.id = id;
    }

    public void run() {
        System.out.println("Client " + id + ": starting thread");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            PrintWriter out = new PrintWriter(sc.getOutputStream(), true);
            String line;
            out.println("SECURE INIT");
            line = in.readLine();
            System.out.println("Client " + id + ": received from server: " + line);
            
        } catch (Exception e) {
            System.out.println("Client " + id + ": ERROR");
        }
    }

        
}

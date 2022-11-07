package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
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
            SecurityFunctionsCliente fc = new SecurityFunctionsCliente();
            BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            PrintWriter out = new PrintWriter(sc.getOutputStream(), true);
            PublicKey publicaServidor = fc.read_kplus("datos_asim_srv.pub",String.valueOf(id));
            String line;
            out.println("SECURE INIT");
            line = in.readLine();
            BigInteger g = new BigInteger(line);

            line = in.readLine();
            BigInteger p = new BigInteger(line);

            line = in.readLine();
            BigInteger g2x = new BigInteger(line);

            line = in.readLine();
            String firma = line;

            
            System.out.println("Client " + id + ": g = " + g);
            System.out.println("Client " + id + ": p = " + p);
            System.out.println("Client " + id + ": g2x = " + g2x);



            
        } catch (Exception e) {
            System.out.println("Client " + id + ": ERROR");
        }
    }

    private byte[] generateIvBytes() {
	    byte[] iv = new byte[16];
	    new SecureRandom().nextBytes(iv);
	    return iv;
	}
	
	private BigInteger G2Y(BigInteger base, BigInteger exponente, BigInteger modulo) {
		return base.modPow(exponente,modulo);
	}
	
	private BigInteger calcular_llave_maestra(BigInteger base, BigInteger exponente, BigInteger modulo) {
		return base.modPow(exponente, modulo);
	}

    public byte[] str2byte( String ss)
	{	
		// Encapsulamiento con hexadecimales
		byte[] ret = new byte[ss.length()/2];
		for (int i = 0 ; i < ret.length ; i++) {
			ret[i] = (byte) Integer.parseInt(ss.substring(i*2,(i+1)*2), 16);
		}
		return ret;
	}
	
	public String byte2str( byte[] b )
	{	
		// Encapsulamiento con hexadecimales
		String ret = "";
		for (int i = 0 ; i < b.length ; i++) {
			String g = Integer.toHexString(((char)b[i])&0x00ff);
			ret += (g.length()==1?"0":"") + g;
		}
		return ret;
	}
        
}

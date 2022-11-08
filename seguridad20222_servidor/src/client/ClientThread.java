package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
           
            PublicKey publicServerKey = fc.read_kplus("seguridad20222_servidor/datos_asim_srv.pub",String.valueOf(id));
            
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
            System.out.println("Client " + id + ": firma = " + firma);

            String msg = g.toString() +","+ p.toString() +","+ g2x;
            byte[] byteFirma = str2byte(firma);

            long startF = System.nanoTime();
            boolean verificacion = fc.checkSignature(publicServerKey, byteFirma, msg);
            long endF = System.nanoTime();
            System.out.println("Tiempo de VERIFICACIÓN DE LA FIRMA (F): " + (endF - startF) + " nanosegundos");

            if (verificacion) {
                System.out.println("Client " + id + ": signature verification - OK");
                out.println("OK");

                //Genera un número aleatorio para usar de exponente y generar g2y
                SecureRandom r = new SecureRandom();
			    int x = Math.abs(r.nextInt());
    		    Long longx = Long.valueOf(x);
    		    BigInteger bix = BigInteger.valueOf(longx);

                long startGy = System.nanoTime();
                BigInteger g2y = G2Y(g, bix, p);
                long endGy = System.nanoTime();
                System.out.println("Tiempo de CALCULO DE g2y (G2Y): " + (endGy - startGy) + " nanosegundos");

                BigInteger g2yServer = g2x.mod(p);
                out.println(g2y);

                //Gerera la llave maestra
                BigInteger llave_maestra = calcular_llave_maestra(g2yServer,bix,p);
                String str_llave = llave_maestra.toString();
    		    System.out.println( "Client " + id + ": Llave maestra: " + str_llave);

                //Genera la llave simétrica
                SecretKey sk_srv = fc.csk1(str_llave);
			    SecretKey sk_mac = fc.csk2(str_llave);

                //Genera vector de inicialización
                byte[] ivByte = generateIvBytes();
                IvParameterSpec iv = new IvParameterSpec(ivByte);
                
                //Cifrado de la consulta
                int consultaInt = ThreadLocalRandom.current().nextInt();
                String consulta = String.valueOf(Math.abs(consultaInt));

                System.out.println("Client " + id + ": Consulta: " + consulta);
                byte[]consultaByte = consulta.getBytes();
                String idStr = String.valueOf(id);
                
                byte[] consulta_cifrada = fc.senc(consultaByte, sk_srv, iv,idStr);
                out.println(byte2str(consulta_cifrada));
  
                //Hash de la consulta
                long start = System.nanoTime();
                byte[] consulta_hash = fc.hmac(consultaByte, sk_mac);
                long end = System.nanoTime();
                System.out.println("Tiempo de CODIGO DE AUTENTICACION (HMAC): " + (end - start) + " nanosegundos");
                out.println(byte2str(consulta_hash));

                //Envía el vector de inicialización
                out.println(byte2str(ivByte));

                //Recibe la respuesta
                line = in.readLine();
                if (line.equals("OK")) {
                    System.out.println("Client " + id + ": Server response: OK");

                    line = in.readLine();
                    byte[] respuesta_cifrada = str2byte(line);

                    line = in.readLine();
                    byte[] respuesta_hash = str2byte(line);

                    line = in.readLine();
                    byte[] ivByte2 = str2byte(line);
                    IvParameterSpec iv2spec = new IvParameterSpec(ivByte2);

                    //Descifra la respuesta
                    byte[] respuesta_descifrada = fc.sdec(respuesta_cifrada, sk_srv, iv2spec);

                    //Verifica la respuesta
                    if (fc.checkInt(respuesta_descifrada, sk_mac, respuesta_hash)) {
                        System.out.println("Client " + id + ": Server response: HMAC OK");
                        out.println("OK");
                    } else {
                        System.out.println("Client " + id + ": Server response: HMAC error");
                        out.println("ERROR");
                    }

                } else {
                    System.out.println("Client " + id + ": Server response: ERROR");
                }

            } else {
                System.out.println("Client " + id + ": signature verification - ERROR");
                out.println("ERROR");
            }
           
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

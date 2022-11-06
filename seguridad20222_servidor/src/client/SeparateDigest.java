package client;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SeparateDigest {
	// Driver code
		public static void main(String args[], String s1,String s2) throws NoSuchAlgorithmException
		{
			int start = 0;
			int length = 44;
			String result = s1.substring(start, length);
			int start1 = 44;
			int length1 = 128;
			String result1 = s1.substring(start1, length1);
			System.out.println("Secret key: " + result1);
			try {
				Hmac.main(args,s2,result1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}


}

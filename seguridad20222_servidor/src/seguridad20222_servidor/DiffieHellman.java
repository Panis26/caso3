package seguridad20222_servidor;
import java.util.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.lang.*;

class DiffieHellman{

public static void main(String args[]){
BigInteger q;
System.out.print("Diffie Hellman Key Exchange Algorithm:\nEnter a prime number q: ");
Scanner sc =  new Scanner(System.in);
q=sc.nextBigInteger();
//prime number check
if((q.isProbablePrime(99999))){
	//get primitive root a of q
	HashMap<BigInteger,HashSet<BigInteger>> primroots = findPrimRoot(q);
	Random rand = new Random();
	int r = rand.nextInt(primroots.size()); //choosing a random number between 1 to size-1
	BigInteger a = (BigInteger)primroots.keySet().toArray()[r];
	System.out.println("\nChosen primitive root a of q: " + a.toString());
	BigInteger Xa,Xb,t1,t2,Ya,Yb;
	System.out.println("Select Private Keys (less than q):-");
	System.out.print("User A (Xa): ");
	Xa=sc.nextBigInteger();
	
	//check Xa value, proceed if Xa < q
	if(Xa.compareTo(q) < 0){
			//calculate public key for user A
			t1 = a.pow(Xa.intValue());
			Ya = t1.mod(q);		
	}
	else {
		System.out.println("Xa value is greater/equal than q");
		return;
	}
	
	System.out.print("User B (Xb): ");
	Xb=sc.nextBigInteger();
	
	//check Xb value, proceed if Xb < q
	if(Xb.compareTo(q) < 0){
			//calculate public key for user B
			t2 = a.pow(Xb.intValue());
			Yb = t2.mod(q);		
	}
	else {
			System.out.println("Xb value is greater/equal than q");
			return;
		}

	//print key pairs for both the users
	System.out.println("Key Pairs (PublicKey, PrivateKey) are:-\nUser A: (" + Ya.toString() + ", " + Xa.toString() + ")");
	System.out.println("User B: (" + Yb.toString() + ", " + Xb.toString() + ")");
	System.out.println("Key Values are:-");
	
	//Key Generation for User A
	BigInteger t3 = Yb.pow(Xa.intValue());
	BigInteger ka = t3.mod(q);
	System.out.println("User A = " + ka.toString());
	
	//Key Generation for User B
	BigInteger t4 = Yb.pow(Xa.intValue());
	BigInteger kb = t4.mod(q);
	System.out.println("User B = " + kb.toString());
	
	if(ka.equals(kb))
		System.out.println("Key Transfer Successful");
							
	try {
		Hash.main(args, ka);
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
else 
	System.out.println("The entered number is not prime.");	
}


public static HashMap<BigInteger,HashSet<BigInteger>> findPrimRoot(BigInteger q){
		HashMap<BigInteger,HashSet<BigInteger>> matrix = new HashMap<BigInteger,HashSet<BigInteger>>();
		BigInteger lastElement = q.subtract(BigInteger.valueOf(1));
		BigInteger length = q.subtract(BigInteger.valueOf(2));
		BigInteger a = new BigInteger("2");
		while(a.compareTo(lastElement) <= 0){
			BigInteger s = new BigInteger("2");
			HashSet<BigInteger> set = new HashSet<BigInteger>();
			while(s.compareTo(lastElement) <= 0){
				BigInteger mo = (a.pow(s.intValue())).mod(q);
				if(set.contains(mo))
					break;	//Duplicate so not a primitive root
				set.add(mo);
				s = s.add(BigInteger.valueOf(1));
			}
			if(length.equals(BigInteger.valueOf(set.size()))){
				matrix.put(a,set);
			}
			a = a.add(BigInteger.valueOf(1));
		}
		//printing all the primitive roots
		System.out.println("The primitive roots are:");
		for(BigInteger key : matrix.keySet())
		System.out.print(key.toString() + " ");
		
		return matrix;
	}

}
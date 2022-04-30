    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sha256rsa;


import java.math.BigInteger;
import java.security.SecureRandom;
	 
	public class RSA
	{
                        /*Keylength is usually 1024-4096 bits. The recommended key length today is 2048 bits, but
                        the longer the key length is, the more time running the program takes, so I set the key-
                        length to 1024.*/
                        private int        keyLength = 1024;
                        private SecureRandom  rand;
	    private BigInteger p;
	    private BigInteger q;
	    private BigInteger n;
	    private BigInteger lambda;
	    private BigInteger e;
	    private BigInteger d;
	    private BigInteger[] publicKey = new BigInteger[2];
	    private BigInteger[] privateKey = new BigInteger[2];
	    private final BigInteger one = new BigInteger("1");
	 
	    public RSA()
	    {
	        rand = new SecureRandom();
	        /*Need to use probablePrime because
	        of the size of the integers. It shouldn't be a problem though, because the chance of
	        probablePrime not generating a prime is extremely low.*/
	        p = BigInteger.probablePrime(keyLength, rand); 
	        q = BigInteger.probablePrime(keyLength, rand);
	        n = p.multiply(q);
	        lambda = p.subtract(one).multiply(q.subtract(one));
	        e = BigInteger.probablePrime(keyLength / 2, rand);
	        while (e.intValue()<lambda.intValue() && (lambda.gcd(e).intValue()-one.intValue()) > 0){
	            e.add(one);
	        }
	        d = e.modInverse(lambda);
	        publicKey[0]=e;
	        publicKey[1]=n;
	        
	        privateKey[0]=d;
	        privateKey[1]=n;

	    }
	    public byte[] encrypt(BigInteger d, BigInteger n, byte[] message)
	    {
	        return (new BigInteger(message)).modPow(d, n).toByteArray();
	    }
	 
	    public byte[] decrypt(BigInteger e, BigInteger n, byte[] message)
	    {
	        return (new BigInteger(message)).modPow(e, n).toByteArray();
	    }
	 //To Convert HASH/CIPHER bytes into String for printing
	    public String toString(byte[] cipher)
	    {
	        String s = "";
	        for (byte b : cipher)	{
	            s += Byte.toString(b);
	        }
	        return s;
	    }
	 
	    public BigInteger[] getPublicKey() {
			return publicKey;
		}

		public BigInteger[] getPrivateKey() {
			return privateKey;
		}

	}

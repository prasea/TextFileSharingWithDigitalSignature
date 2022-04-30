/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA;

import static RSA.RSA2.extEuclid;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author Prajanya
 */
public class FinalRSA {
    private int keyLength = 1024;
    private static SecureRandom  rand;
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private static BigInteger e;
    private BigInteger d;
    private BigInteger[] publicKey = new BigInteger[2];
    private BigInteger[] privateKey = new BigInteger[2];
    private final BigInteger one = new BigInteger("1");
    
    public static void main(String[] args) {
        FinalRSA rsa = new FinalRSA();        
    }
    public FinalRSA()
    {
         rand = new SecureRandom();
         p = BigInteger.probablePrime(keyLength, rand); 
        q = BigInteger.probablePrime(keyLength, rand);
        n = p.multiply(q);
        phi = p.subtract(one).multiply(q.subtract(one) );
        e = generateE(phi);
//        d = e.modInverse(phi);
        d = extendedEuclidean(e, phi)[1];
        
        publicKey[0]=e;
        publicKey[1]=n;

        privateKey[0]=d;
        privateKey[1]=n;
    }
       /**
     * generate e by finding a Phi such that they are coprimes (gcd = 1)
     *
     */
    public static BigInteger generateE(BigInteger phi) {        
        rand = new SecureRandom();        
        do {
            e = new BigInteger(1024, rand);
            while (e.min(phi).equals(phi)) { // while phi is smaller than e, look for a new e
                e = new BigInteger(1024, rand);
            }
        } while (!gcd(e, phi).equals(BigInteger.ONE)); // if gcd(e,phi) isnt 1 then stay in loop
        return e;
    }
       /**
     * Recursive implementation of Euclidian algorithm to find greatest common
     * denominator Note: Uses BigInteger operations
     */
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return a;
        } else {
            return gcd(b, a.mod(b));
        }
    }
    /**
     * Recursive EXTENDED Euclidean algorithm, solves Bezout's identity (ax + by
     * = gcd(a,b)) and finds the multiplicative inverse which is the solution to
     * ax ≡ 1 (mod b) returns [d, p, q] where d = gcd(a,b) and ap + bq = d Note:
     * Uses BigInteger operations
     */
    public static BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[]{
                a, BigInteger.ONE, BigInteger.ZERO
            }; // { a, 1, 0 }
        }
        BigInteger[] vals = extEuclid(b, a.mod(b));
        BigInteger d = vals[0];
        BigInteger p = vals[2];
        BigInteger q = vals[1].subtract(a.divide(b).multiply(vals[2]));
        return new BigInteger[]{
            d, p, q
        };
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

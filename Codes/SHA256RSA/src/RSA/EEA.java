/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RSA;

import static RSA.RSA2.extEuclid;
import java.math.BigInteger;

/**
 *
 * @author Prajanya
 */
public class EEA {
        public static void main(String[] args) {
        BigInteger d = extendedEuclidean(BigInteger.valueOf(13), BigInteger.valueOf(60))[1];
            System.out.println(d);            
    }
    
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
}

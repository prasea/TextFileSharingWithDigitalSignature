/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.generate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 *
 * @author Prajanya
 */
public class Client {

    private static BigInteger n = null;
    private static BigInteger encryptD = null;
    private static BigInteger phi = null;
    private static BigInteger e = null;
    private static BigInteger encryptE = null;
    private static BigInteger d = null;

    public static void main(String[] args) {
        handleInput();
        System.out.println("The public key is " + e);
        d = calculateD(phi, n, e);
        System.out.println("The private key is " + d);        
        sendMessage("Hello world");
    }

    static boolean isPrime(BigInteger number) {
        return number.isProbablePrime(1000);
    }

    static BigInteger calculatePhi(BigInteger p, BigInteger q) {
        return ((p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1))));
    }

    static boolean isRelativelyPrime(BigInteger e, BigInteger phi) {
        return e.gcd(phi).intValue() == 1;
    }
    //calulating private key

    static BigInteger calculateD(BigInteger phi, BigInteger n, BigInteger e) {
        return e.modInverse(phi);
    }
    static int characterToAscii(char character){
        return (int)character;
    }
    static char asciiToCharacter(int ascii){
        return (char)ascii;
    }
 static BigInteger[] signMessage(String x, BigInteger d, BigInteger n)
    {
        String[] xSplit = x.split(" ");
        BigInteger[] xPrime = new BigInteger[xSplit.length];
        IntStream.range(0, xSplit.length).forEach(i -> xPrime[i] = new BigInteger(xSplit[i]).modPow(d,n));
        System.out.println("The array of ASCII signed message with key "+ d+" as==> s="+Arrays.toString(xPrime));
        return xPrime;
    }
 static  StringBuffer sendMessage(String msg)
 {
     String m = msg;
        StringBuffer x = new StringBuffer();
        IntStream.range(0, m.length()).forEach(i -> x.append(characterToAscii(m.charAt(i))+" "));
        System.out.println("The ASCII of message is "+x); //If m = "Hello World", x = [72 101 108 108 111 32 119 111 114 108 100] 
         BigInteger[] xPrime = signMessage(x.toString(), d, n); // Siganture for m="Hello World", [11, 82, 75, 75, 76, 2, 84, 76, 4, 75, 81]
        StringBuffer xPrimeSB = new StringBuffer();
            for(int i=0; i < xPrime.length; i++)
            {
                xPrimeSB.append(xPrime[i].toString()+ " ");
            }
            return xPrimeSB;
 }

    public static void handleInput() {
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        BigInteger p = new BigInteger("0"), q = new BigInteger("0");
        while (flag) {
            System.out.println("Enter 2 primes p != q (seperate with  space)");
            p = sc.nextBigInteger();
            q = sc.nextBigInteger();
            if (!p.equals(q) && isPrime(p) && isPrime(q)) {
                flag = false;
            } else {
                System.out.println(" p & q must be distinct prime numbers");
            }
        }
        n = p.multiply(q);
        phi = calculatePhi(p, q);
        while (!flag) {
            System.out.println("Pick public exponent e from set {1,2,...,phi(n)-1}");
            System.out.println("where inverse of e exists i.e. gcd(e, phi(n)) = 1   ");

            BigInteger input = sc.nextBigInteger();
            if (isRelativelyPrime(input, phi) && input.compareTo(new BigInteger("1")) >= 0 && input.compareTo(phi.subtract(new BigInteger("1"))) <= 0) {
                e = input;
                flag = true;
            }
        }
    }
}

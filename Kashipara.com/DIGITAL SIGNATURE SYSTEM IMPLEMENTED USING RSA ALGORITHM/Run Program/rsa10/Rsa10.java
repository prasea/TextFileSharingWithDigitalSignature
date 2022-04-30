import java.math.BigInteger;
import java.security.*;

class Rsa10{
  //This is a static inner class whose sole
  // purpose is to create an object that serves
  // as a container for keys.  It was made static
  // so that it can be instantiated from within
  // main.  It contains precomputed values for
  // the two keys, e and d, and the modulus
  // operand n.
  //
  // e is the public key
  // d is the private key
  static class Keys{
    BigInteger n;
    BigInteger d;
    BigInteger e;
  }//end inner class Keys

  public static void main(String[] args){
    //Instantiate an object containing
    // Alice's keys.
    Keys aliceKeys = new Keys();
    //Note, these key values were designed to
    // be used with a block size of 12
    aliceKeys.n = new BigInteger("951386374109");
    aliceKeys.d = new BigInteger("279263220413");
    aliceKeys.e = new BigInteger("17");
    
    //Instantiate an object containing
    // Bob's keys
    Keys bobKeys = new Keys();
    //Note, these key values were designed to
    // be used with a block size of 12
    bobKeys.n = new BigInteger("951386374109");
    bobKeys.d = new BigInteger("799574694235");
    bobKeys.e = new BigInteger("19");

    //Test msg.
    String aliceMsg="Hello Bob, how are you?  I "
      + "am going to go to the movie tonight. I "
      + "plan to see Gone with the Wind.  I "
      + "really like Clark Gable.  Would you "
      + "like to go with me?  I would really "
      + "like to have some company.  Your "
      + "friend, Alice.";

    //Set blockSize.
    int blockSize = 12;

    //Instantiate an object of this class
    Rsa10 obj = new Rsa10();

    //Display the key values along with the
    // modulus operand.
    System.out.println("1. Alice's keys:");
    System.out.println("2. e: " + aliceKeys.e);
    System.out.println("3. d: " + aliceKeys.d);
    System.out.println("4. n: " + aliceKeys.n);
    
    System.out.println("\n5. Bob's keys:");
    System.out.println("6. e: " + bobKeys.e);
    System.out.println("7. d: " + bobKeys.d);
    System.out.println("8. n: " + bobKeys.n);
    
    System.out.println("\n9. Block size: " 
                                    + blockSize);
    
    //Display the message that Alice will sign 
    // and send to Bob.
    System.out.println("\n10. Alice's msg: "
                                     + aliceMsg);
                                      
    //Get the msg digest
    byte[] aliceDigest = 
               obj.digestIt(aliceMsg.getBytes());
    
    //Display the msg digest as a string of hex
    // characters
    System.out.println(
           "\n11. Alice's msg digest: " 
           + obj.byteArrayToHexStr(aliceDigest));

    //Extend the length of the digest to force it
    // to be a multiple of the block size.
    int tailLen = aliceDigest.length % blockSize;
    int extendLen = 0;
    if((tailLen > 0)){
                 extendLen = blockSize - tailLen;
    }//end if
    
    //Create an array for the extended digest
    // with all elements initialized to a value
    // of 0.
    byte[] aliceExtendedDigest = 
        new byte[aliceDigest.length + extendLen];
    //Copy the digest into the bottom of the new
    // array.  The empty portion at the top of
    // the array becomes the extension.
    System.arraycopy(aliceDigest,0,
                     aliceExtendedDigest,0,
                     aliceDigest.length);

    //Convert the extended digest into a String
    // of hex characters.  This is necessary to
    // ensure that all of the bytes are
    // compatible with the characters supported
    // by the encode method.
    String aliceExtendedDigestAsHex = 
      obj.byteArrayToHexStr(aliceExtendedDigest);
    //Display the extended msg digest
    System.out.println(
            "\n12. Alice's extended msg digest: "
                    + aliceExtendedDigestAsHex);

    //Alice encodes the extended msg digest into
    // numeric format in preparation for
    // encryption.
    String aliceEncodedDigest = 
            obj.encode(aliceExtendedDigestAsHex);

    //Alice creates a digital signature by
    // encrypting the encoded msg digest using
    // her private key.
    String aliceSignature = obj.doRSA(
      aliceEncodedDigest,aliceKeys.d,aliceKeys.n,
                                      blockSize);
    System.out.println(
      "\n13. Alice's encrypted digital " 
                + "signature: "+ aliceSignature);

    //Alice signs the msg by appending the
    // digital signature onto the msg.
    String aliceSignedMsg = 
                       aliceMsg + aliceSignature;
    System.out.println(
                     "\n14. Alice's signed msg: "
                               + aliceSignedMsg);
                                    
    //Because she needs to maintain
    // confidentiality, Alice will encrypt the
    // signed message using Bob's public key
    // before sending the message to him.  In
    // preparation for encoding and encryption,
    // she needs to extend the length of the
    // signed message such that the total length
    // is a multiple of half the block size.  In
    // addition, she needs to notify Bob as to
    // the length of the original message so
    // that he will have a way to separate the
    // text of the original message from the
    // encrypted message signature.  She will
    // accomplish both of these requirements at
    // the same time.  She will pad the signed
    // message using a pad that includes the
    // length of the original message in the
    // last four bytes of the pad.  All bytes in
    // the pad other than the last four bytes
    // are filled with equal characters (=).
    String alicePaddedSignedMsg = 
        obj.padTheMsg(aliceSignedMsg,blockSize/2,
                              aliceMsg.length());
    System.out.println(
      "\n15. Alice's padded signed msg with msg "
                         + "length appended: " 
                         + alicePaddedSignedMsg);
      
    //Alice encodes and encrypts the padded
    // signed message using Bob's public key.
    
    //Encode the message.
    String aliceEncodedSignedMsg = 
                obj.encode(alicePaddedSignedMsg);

    //Encrypt the message.
    String aliceEncryptedSignedMsg = obj.doRSA(
       aliceEncodedSignedMsg,bobKeys.e,bobKeys.n,
                                      blockSize);
    System.out.println(
     "\n16. Alice's encrypted signed message: "
                      + aliceEncryptedSignedMsg);
                      
    //************
    //Alice sends the encrypted signed msg to
    // Bob.  
    //************
    
    String bobEncryptedSignedMsg = 
                         aliceEncryptedSignedMsg;
    
    //At the receiving end, Bob begins by
    // decrypting and decoding the signed
    // message using his private key.

    //Decrypt the message.
    String bobDecryptedSignedMsg = obj.doRSA(
       bobEncryptedSignedMsg,bobKeys.d,bobKeys.n,
                                      blockSize);
    //Decode the message.
    String bobDecodedSignedMsg = 
               obj.decode(bobDecryptedSignedMsg);
    
    System.out.println(
     "\n17. Bob's decrypted and decoded signed "
            + "message: "+ bobDecodedSignedMsg);
    
    //Bob extracts the message length, the 
    // message text, and the digital signature
    // from the decoded signed msg.

    //Extract the message length.
    int bobMsgLen = Integer.parseInt(
            bobDecodedSignedMsg.substring(
              bobDecodedSignedMsg.length() - 4));
    System.out.println(
               "\n18. Bob's calculated msg len: "
                                    + bobMsgLen);
 
    //Extract the message text.
    String bobExtractedMsgText =
                   bobDecodedSignedMsg.substring(
                                    0,bobMsgLen);
    System.out.println(
               "\n19. Bob's extracted msg text: "
                          + bobExtractedMsgText);
                             
    //Bob knows that everything between the
    // message text and the first equal sign in
    // the decoded signed message is the
    // extended, encoded, and encrypted digital
    // signature.  He also knows that if there
    // are no equal signs the last four bytes
    // containing the length of the original
    // message need to be discarded.  He uses
    // this knowledge to extract the encrypted
    // signature.

    String bobExtractedEncryptedSignature;
    if(bobDecodedSignedMsg.indexOf("=") != -1){
       bobExtractedEncryptedSignature =
         bobDecodedSignedMsg.substring(
           bobMsgLen,bobDecodedSignedMsg.indexOf(
                                           "="));
    }else{
      bobExtractedEncryptedSignature =
             bobDecodedSignedMsg.substring(
               bobMsgLen,
               bobDecodedSignedMsg.length() - 4);
    }//end else
    
    System.out.println(
        "\n20. Bob's extracted extended digital "
             + "signature: " 
               + bobExtractedEncryptedSignature);

    //Now Bob has the message length, the
    // message, and the encrypted digital
    // signature.

    //Bob decrypts the extended digital
    // signature using Alice's public key.
    String bobDecryptedExtendedSignature =
        obj.doRSA(bobExtractedEncryptedSignature,
              aliceKeys.e,aliceKeys.n,blockSize);

    //Bob decodes the extended digital signature.
    String bobDecodedExtendedSignature = 
       obj.decode(bobDecryptedExtendedSignature);

    System.out.println(
          "\n21. Bob's decoded extended digital "
                + "signature: "
                  + bobDecodedExtendedSignature);
                             
    //Bob knows that the digital signature
    // consists of only 40 hex digits and that
    // any additional characters were put there
    // to fill out the last block and make
    // encryption possible.  He extracts the hex
    // version of the digital signature by
    // ignoring all but the first 40 hex
    // characters.
    String bobDecodedSignature = 
        bobDecodedExtendedSignature.substring(
                                           0,40);
    System.out.println(
        "\n22. Bob's decoded digital signature: "
                          + bobDecodedSignature);
    
    //Bob computes the msg digest for comparison
    // with the decoded signature.
    byte[] bobDigest = obj.digestIt(
                 bobExtractedMsgText.getBytes());
    
    System.out.println("\n23. Bob's digest: " 
             + obj.byteArrayToHexStr(bobDigest));
                             
    //Bob compares the decoded digital signature
    // with his computed msg digest and displays
    // an output message indicating whether or
    // not the signature is valid.
    if(bobDecodedSignature.equals(
             obj.byteArrayToHexStr(bobDigest))){
      System.out.println(
                       "\n24. Bob's conclusion: "
                            + "Valid signature");
    }else{
      System.out.println(
                       "\n24. Bob's conclusion: "
                          + "Invalid signature");
    }//end else

  }//end main
  //-------------------------------------------//

  //The purpose of this method is to encode a
  // plain text msg into numeric format
  // where:
  // space = 32 - 32 = 0
  // A = 65 - 32 = 33
  // ...
  // Z = 90 - 32 = 58
  // ...
  // a = 97 - 32 = 65
  // ...
  // ~ = 126 - 32 = 94

  //Note that this encoding method supports all
  //of the ASCII characters from space through
  // tilde (~) inclusive.
  String encode(String msg){
    byte[] textChars = msg.getBytes();
    String temp = "";
    String encodedMsg = "";

    //Build the encoded text string two numeric
    // characters at a time.  Each msg
    // character is converted into two numeric
    // characters according to the relationships
    // given above.
    for(int cnt = 0; cnt < msg.length();
                                          cnt++){
      temp = String.valueOf(
                       textChars[cnt] - ' ');
      //Convert all single-character numeric
      // values to two characters with a leading
      // zero, as in 09.
      if(temp.length() < 2) temp = "0" + temp;
      encodedMsg += temp;
    }//end for loop
    return encodedMsg;
  }//end encode
  //-------------------------------------------//

  //The purpose of this method is to reverse the
  // encoding process implemented by the encode
  // method, converting a string of numeric
  // characters back to a text string containing
  // the ASCII characters from space through
  // tilde.
  String decode(String encodedMsg){
    String temp = "";
    String decodedText = "";
    for(int cnt = 0; cnt < encodedMsg.length();
                                       cnt += 2){
      temp = encodedMsg.substring(cnt,cnt + 2);
      //Convert two numeric text characters to a
      // value of type int.
      int val = Integer.parseInt(temp) + 32;
      //Convert the ASCII character values to
      // numeric String values and build the
      // output String one character at a time.
      decodedText += String.valueOf((char)val);
    }//end for loop
    return decodedText;
  }//end decode
  //-------------------------------------------//

  //Apply the RSA algorithm to an input string
  // using the exponent exp and the modulus
  // operator n, which are provided as input
  // parameters.  This method can be used to
  // encrypt or to decipher the input string
  // depending on whether the exponent is an
  // encryption key or a decryption key.  Apply
  // the algorithm for the block size given by
  // the incoming parameter named blockSize.
  String doRSA(String inputString,
               BigInteger exp,
               BigInteger n,
               int blockSize){

    BigInteger block;
    BigInteger output;
    String temp = "";
    String outputString = "";

    //Iterate and process one block at a time.
    for(int cnt = 0; cnt < inputString.length();
                               cnt += blockSize){
      //Get the next block of characters
      // and encapsulate them in a BigInteger
      // object.
      temp = inputString.substring(
                            cnt,cnt + blockSize);

      block = new BigInteger(temp);
      //Raise the block to the power exp, apply
      // the modulus operand n, and save the
      // remainder.  This is the essence of the
      // RSA algorithm.
      output = block.modPow(exp,n);

      //Convert the numeric result to a
      // four-character string, appending leading
      // zeros as necessary.
      temp = output.toString();
      while(temp.length() < blockSize){
        temp = "0" + temp;
      }//end while

      //Build the outputString blockSize
      // characters at a time.  Each character
      // in the inputString results in one
      // character in the outputString.
      outputString += temp;
    }//end for loop

    return outputString;
  }//end doRSA
  //-------------------------------------------//
  
  //This method generates and returns a digest
  // for an incoming array of bytes using Sun's
  // SHA msg digest algorithm..
  byte[] digestIt(byte[] dataIn){
    byte[] theDigest = null;
    try{
      //Create a MessageDigest object
      // implementing the SHA algorithm, as
      // supplied by SUN
      MessageDigest msgDigest = 
         MessageDigest.getInstance("SHA", "SUN");
      //Feed the byte array to the digester.  Can
      // accommodate multiple calls if needed
      msgDigest.update(dataIn);
      //Complete the digestion and save the
      // result
      theDigest = msgDigest.digest();
    }catch(Exception e){System.out.println(e);}

    //Return the digest value to the calling
    // method as an array of bytes.
    return theDigest;
  }//end digestIt()
  //-------------------------------------------//
  
  //This method converts an incoming array of
  // bytes into a string that represents each of
  // the bytes as two hex characters.
  String byteArrayToHexStr(byte[] data){
    String output = "";
    String tempStr = "";
    int tempInt = 0;
    for(int cnt = 0;cnt < data.length;cnt++){
      //Deposit a byte into the 8 lsb of an int.
      tempInt = data[cnt]&0xFF;
      //Get hex representation of the int as a
      // string.
      tempStr = Integer.toHexString(tempInt);
      //Append a leading 0 if necessary so that
      // each hex string will contain two
      // characters.
      if(tempStr.length() == 1)
                         tempStr = "0" + tempStr;
      //Concatenate the two characters to the
      // output string.
      output = output + tempStr;
    }//end for loop
    return output.toUpperCase();
  }//end byteArrayToHexStr
  //-------------------------------------------//
  //This method pads a message such that the
  // final length is a multiple of block and the
  // last four bytes specify the origMsgLength.
  // All the remaining bytes in the pad are
  // filled with equal characters (=). The padded
  // message is returned as type String.
  private String padTheMsg(
          String msgIn,int block,int origMsgLen){
    byte[] msgData = msgIn.getBytes();
    int msgInLen = msgData.length;
    int tailLength = msgInLen%block;
    int padLength = 0;
    if((block - tailLength >= 4))
      padLength = block - tailLength;
    else 
      padLength = 2*block - tailLength;
      
    //Create a four-byte array containing
    // characters that indicate the length of
    // the original msg with leading zeros if
    // necessary.
    String msgLenAsStr = "" + origMsgLen;
    //Confirm number of characters.
    if((msgLenAsStr.length() > 4) 
                 || (msgLenAsStr.length() <= 0)){
      System.out.println(
                        "Message length error.");
      System.exit(0);
    }//end if
    
    //Prepend leading zeros if necessary
    if(msgLenAsStr.length() == 1){
      msgLenAsStr = "000" + msgLenAsStr;
    }else if(msgLenAsStr.length() == 2){
      msgLenAsStr = "00" + msgLenAsStr;
    }else if(msgLenAsStr.length() == 3){
      msgLenAsStr = "0" + msgLenAsStr;
    }else if(msgLenAsStr.length() == 1){
      msgLenAsStr = "000" + msgLenAsStr;
    }//end else
    
    //Create a four-byte array containing the
    // original message length as four numeric
    // characters with leading zeros.
    byte[] msgLenAsBytes = 
                          msgLenAsStr.getBytes();
      
    //Construct an array containing the bytes
    // required to make the padded message length
    // equal to a multiple of block
    byte[] thePad = new byte[padLength];

    //Populate the array with = characters. 
    // No need to put = characters in the last
    // four bytes.
    for(int cnt = 0;cnt < thePad.length - 4;
                                          cnt++){
      thePad[cnt] = '=';
    }//end for loop
    
    //Put the original message length at the end
    // of the pad.
    System.arraycopy(msgLenAsBytes,0,thePad,
                            thePad.length - 4,4);
    
    //Create an output array.
    byte[] output = 
                  new byte[msgInLen + padLength];

    //Populate the output array with the original
    // msgData concatenated with the pad.
    System.arraycopy(msgData,0,output,0,
                                       msgInLen);
    System.arraycopy(
       thePad,0,output,msgInLen,thePad.length);
       
    //Convert the output to a String and return
    // the String..
    String outputAsStr = new String(output);
    return outputAsStr;

  }//end padTheMsg
  //-------------------------------------------//
}//end class Rsa10
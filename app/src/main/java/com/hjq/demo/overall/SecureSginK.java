package com.hjq.demo.overall;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureSginK {
    public  String md5(String input) throws NoSuchAlgorithmException {
        String result = input;
        if(input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while(result.length() < 32) { //31位string
                result = "0" + result;
            }
            result="sb"+result+"cXK"+result.substring(0,9);
            md.update(result.getBytes());
            BigInteger hash1 = new BigInteger(1, md.digest());
            result = hash1.toString(16);
            while(result.length() < 32) { //31位string
                result = "0" + result;
            }
            result="jb8cn"+result.substring(0,27)+"tM0mb"+result.substring(0,3)+"./";
            md.update(result.getBytes());
            BigInteger hash2 = new BigInteger(1, md.digest());
            result = hash2.toString(16);
            while(result.length() < 32) { //31位string
                result = "0" + result;
            }
        }
        return result;
    }
}

package Anon.client.service.utils;

import java.security.SecureRandom;

/**
 *
 * Some utility functions for generating random strings and numbers
 */
public class Random {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    /**
     * Generate random strings of certain length.
     *
     * @param len the target length of the random string
     * @return the random string with target length
     */
    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}

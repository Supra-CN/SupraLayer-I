/**
 * 这个类对android自带的Base64(added in API 8)进行了一些封装处理,目前暂时不需要关心flag
 */

package cn.supra.supralayer_i.util;

import java.io.UnsupportedEncodingException;

public class Base64 {
	private static final int NO_WRAP = android.util.Base64.NO_WRAP;

	/**
     * encode the input data producong a base 64 encoded byte array.
     * 
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode(byte[] input) {
       return android.util.Base64.encode(input, NO_WRAP);
    }

  
    /**
     * decode the base 64 encoded input data.
     * 
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(byte[] input) {
    	return android.util.Base64.decode(input, NO_WRAP);
    }

 
    /**
     * Decode the string and convert back the decoded value into a string
     * using the specified charset. 
     * Use default encoding if charset is null or invalid.
     */
    public static String decode(String data, String charset) {
    	String output = null;
    	if (null == charset) {
    		output =  new String(android.util.Base64.decode(data, NO_WRAP));
    	} else {
    		try {
    			output = new String(android.util.Base64.decode(data, NO_WRAP), charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    	return output;
    }

    /**
     * Decode the string and convert back the decoded value into a string
     * using the specified charset. 
     * Use default encoding if charset is null or invalid.
     */
    public static String decode(byte[] data, String charset) {
        String output = null;
    	if (null == charset) {
        	output = new String(android.util.Base64.decode(data, NO_WRAP));
        } else {
        	try {
				output = new String(android.util.Base64.decode(data, NO_WRAP), charset);
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			}
        }
    	return output;
    }

  

    


   
}

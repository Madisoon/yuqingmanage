package com.alienlab.utils;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Md5Azdg {
	private static  String key;
	static{
		key = "pop";//ConfigManager.getInstance().getConfigMap().get("password_key");
	}
	/**
	 * 加密算法
	 * 
	 * @param txt
	 * @return
	 */
	public static String encrypt(String txt) {
		String encrypt_key = "ioooo";
		int ctr = 0;
		String tmp = "";
		
		for (int i = 0; i < txt.length(); i++) {
			ctr = (ctr == encrypt_key.length()) ? 0 : ctr;
			tmp = tmp + encrypt_key.charAt(ctr)
					+ (char) (txt.charAt(i) ^ encrypt_key.charAt(ctr));
			ctr++;
		}
		return base64_encode(key(tmp, key));
	}
	private static String key(String txt, String encrypt_key) {
		encrypt_key = strMD5(encrypt_key);
		int ctr = 0;
		String tmp = "";
		
		for (int i = 0; i < txt.length(); i++) {
			ctr = (ctr == encrypt_key.length()) ? 0 : ctr;
			int c = txt.charAt(i) ^ encrypt_key.charAt(ctr);
			String x = "" + (char) c;
			tmp = tmp + x;
			ctr++;
		}

		return tmp;

	}

	private static String base64_encode(String str) {
		return new sun.misc.BASE64Encoder().encode(str.getBytes());
	}

	private static String base64_decode(String str) {
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		
		if (str == null)
			return null;
		try {
			return new String(decoder.decodeBuffer(str));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String strMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'h', 'o', 't' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}

			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/**
	 * 前端解密
	 * @param plainText
	 * @return
	 */
	public static String md5s(String plainText){
		String str="";
		try {
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte bytes[]=md.digest();
			int i;

			StringBuffer buf = new StringBuffer("");
		    for (int offset = 0; offset < bytes.length; offset++) {
		    	i = bytes[offset];
		    	if (i < 0)
		    		i += 256;
		    	if (i < 16)
		    		buf.append("0");
		    	buf.append(Integer.toHexString(i));
		    }
		    str = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}
    
}

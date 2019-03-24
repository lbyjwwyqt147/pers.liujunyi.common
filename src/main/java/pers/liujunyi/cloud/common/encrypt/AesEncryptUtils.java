package pers.liujunyi.cloud.common.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/***
 *
 * @author ljy
 */
public class AesEncryptUtils {
	private static final String KEY = "d7b85f6e214abcda";
	private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

	/**
	 * base64 加密
	 * @param bytes
	 * @return
	 */
	public static String base64Encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * base64 解密
	 * @param base64Code
	 * @return
	 */
	public static byte[] base64Decode(String base64Code) throws Exception {
		return Base64.decodeBase64(base64Code);
	}

	public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
		return cipher.doFinal(content.getBytes("utf-8"));
	}

	/**
	 * 对数据进行加密
	 * @param content  要加密的数据
	 * @param encryptKey 密钥
	 * @return
	 * @throws Exception
	 */
	public static String aesEncrypt(String content, String encryptKey) throws Exception {
		return base64Encode(aesEncryptToBytes(content, encryptKey));
	}

	/**
	 * 解密数据
	 * @param encryptBytes
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes);
	}

	/**
	 * 对加密数据 进行解密
	 * @param encryptStr
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
		return aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
	}

	public static void main(String[] args) throws Exception {
		String content = "你好";
		System.out.println("加密前：" + content);

		String encrypt = aesEncrypt(content, KEY);
		System.out.println(encrypt.length() + ":加密后：" + encrypt);

		String decrypt = aesDecrypt(encrypt, KEY);
		System.out.println("解密后：" + decrypt);
	}
	
}
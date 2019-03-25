package pers.liujunyi.cloud.common.encrypt;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * RSA公钥/私钥/签名工具包
 *
 *
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 *
 * @author ljy
 * @date 2019-03-25
 * @version 1.0
 */
@Log4j2
public class RsaUtils {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static KeyFactory keyFactory = null;

    static {
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    /**
     * 生成密钥对(公钥和私钥)
     *
     * @return
     * @throws Exception
     */
    public static void genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        System.out.println("RSA 公钥:" + publicKey);
        System.out.println("RSA 私钥:" + privateKey);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 待签名的数据
     * @param privateKey 私钥(BASE64编码)
     *
     * @return 签名
     * @throws Exception
     */
    public static String sign(String data, String privateKey) throws Exception {
        Key decodePrivateKey = getPrivateKeyFromBase64KeyEncodeStr(privateKey);
        Signature signature = SignakeyFactoryure.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(decodePrivateKey);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 校验数字签名
     *
     * @param data 原始数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return 是否签名通过
     * @throws Exception
     *
     */
    public static boolean verify(String data, String publicKey, String sign)
            throws Exception {
        PublicKey key = getPublicKeyFromBase64KeyEncodeStr(publicKey);
        Signature signature = Signature.getInstance("SIGNATURE_ALGORITHM");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    /**
     * 私钥 解密方法
     * @param data 要解密的数据
     * @param privateKey
     * @return 解密后的原数据
     * @throws Exception
     */
    public static String decrypt(String data, String privateKey) throws Exception{
        //要加密的数据
        System.out.println("要解密的数据:" + data);
        //对私钥解密
        Key decodePrivateKey = getPrivateKeyFromBase64KeyEncodeStr(privateKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
        byte[] encodedData = Base64.decode(data);
        byte[] decodedData = cipher.doFinal(encodedData);
        String decodedDataStr = new String(decodedData,"utf-8");
        System.out.println("私钥解密后的数据:"+decodedDataStr);
        return decodedDataStr;
    }

    /**
     * 获取base64加密后的字符串的原始私钥
     * @param key  私钥base64字符串
     * @return
     */
    public  static Key getPrivateKeyFromBase64KeyEncodeStr(String key) {
        byte[] keyBytes = Base64.decode(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        Key privateKey = null;
        try {
            privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 获取base64加密后的字符串的原始公钥
     * @param key  公钥base64字符串
     * @return
     */
    public static Key getPublicKeyFromBase64KeyEncodeStr(String key) {
        byte[] keyBytes = Base64.decode(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        Key publicKey = null;
        try {
            publicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }


    /**
     * 公钥加密方法
     * @param primitiveData 要加密的数据
     * @param publicKey 公钥base64字符串
     * @return 加密后的base64字符串
     * @throws Exception
     */
    public static String encryptPublicKey(String primitiveData, String publicKey) throws Exception{
        //要加密的数据
        System.out.println("要加密的数据:"+ primitiveData);
        byte[] data = primitiveData.getBytes();
        // 对公钥解密
        Key decodePublicKey = getPublicKeyFromBase64KeyEncodeStr(publicKey);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey);
        byte[] encodedData = cipher.doFinal(data);
        String encodedDataStr = new String(Base64.encode(encodedData));
        System.out.println("公钥加密后的数据:"+encodedDataStr);
        return encodedDataStr;
    }


    /**
     * 使用公钥进行分段加密
     * @param primitiveData 要加密的数据
     * @return 公钥base64字符串
     * @throws Exception
     */
    public static String encryptByPublicKey(String primitiveData, String publicKey) throws Exception {
        //要加密的数据
        System.out.println("要加密的数据:"+primitiveData);
        byte[] data = primitiveData.getBytes();
        // 对公钥解密
        Key decodePublicKey = getPublicKeyFromBase64KeyEncodeStr(publicKey);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, decodePublicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        String encodedDataStr = new String(Base64.encode(encryptedData));
        System.out.println("公钥加密后的数据:"+encodedDataStr);
        return encodedDataStr;
    }


    /**
     * 使用私钥进行分段解密
     * @param data 使用base64处理过的密文
     * @param privateKey  私钥
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, String privateKey)
            throws Exception {
        byte[] encryptedData = Base64.decode(data);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key decodePrivateKey = getPrivateKeyFromBase64KeyEncodeStr(privateKey);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, decodePrivateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        String decodedDataStr = new String(decryptedData,"utf-8");
        System.out.println("私钥解密后的数据:"+decodedDataStr);
        return decodedDataStr;
    }


    public static void main(String[] args) throws Exception {

        //获取公钥/私钥
        genKeyPair();
        String publicKey = "";
        String privateKey = "";
    }

}

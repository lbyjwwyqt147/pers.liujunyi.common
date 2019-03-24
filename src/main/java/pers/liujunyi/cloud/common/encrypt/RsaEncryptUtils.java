package pers.liujunyi.cloud.common.encrypt;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 加密 解密 工具类
 *
 * @author ljy
 */
public class RsaEncryptUtils {

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        System.out.println("公钥：" + publicKeyString);
        System.out.println("私钥：" + privateKeyString);

    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
     //   genKeyPair();
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ6bNnAgmUZ615mmqo1FzdS+f+mdV6U4bP3fZgyy0mvmauVOveR/w/QusgP1NLdWHP/f5S/E566fU5IfH9qFnBJVlJ4evSHxAbuQT1O4wqBvGzIDQBUZ72IXquYkkQAmQ1bShplGzZ/uTKAtTe53fjQY1Pe8ve23FQpHLDqbAbE7AgMBAAECgYEAmSzzVTTWcq/emsDQhCF/GNBl/nt7ktA8yq4/A1KMT7K3WABE+nG+EIwsVhAxOkKLPT7BO/Ihgo7TChLPE57ny26g447okGGjR3Xk7M3zxXBtsUIdgFL/zTYvBZubQp7/Jnhqtx5aABt+BHNiInmxrzVW1fIM34FlbqwVOVlbeCECQQDtokziFS8xSqPVWT+5LEksZPsW0h9BLm9kjEHnbYcOs5I3cNdcgaRnboo3+jAyrlKQZWeEYtH3wVLKac0+uq0RAkEAqt1QAyDIK3JYSGC+5AU7eAIMroKu+lHrssqHeoVsES8eUpdnTU1RyB2l7mBBBqP+F6Q/i57UjUMgDGDhPw4piwJAbenZF+Vmi60TdHYwhEzYl6EphlewPyzkNySswwelJYanhd86rb4FNhYp9lRRgM+ivsk4hUJUDf2sdpVTasVbkQJAGrfQsyyFIIYY1/iI4Q8QZYusf/1fbzUwLh0NYlNKusUrcK7MMIJOr4QzVjsm7+rk6L1+Uk7b3dsJP+ZGUp3cUQJAYILnmJujQBDafErJ+4yQlh2q5X3+f4xH1OCViBvhMo3leNUQBGD3IZhVkLqxP6B/B2xAYfnEvM2zd4sqhvUoVg==";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCemzZwIJlGeteZpqqNRc3Uvn/pnVelOGz932YMstJr5mrlTr3kf8P0LrID9TS3Vhz/3+UvxOeun1OSHx/ahZwSVZSeHr0h8QG7kE9TuMKgbxsyA0AVGe9iF6rmJJEAJkNW0oaZRs2f7kygLU3ud340GNT3vL3ttxUKRyw6mwGxOwIDAQAB";
        //加密字符串
        String message = "https://blog.csdn.net/qy20115549/article/details/83105736";
        String messageEn = encrypt(message, publicKey);
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn, privateKey);
        System.out.println("还原后的字符串为:" + messageDe);

        System.out.println("1111:" + decrypt("CE+mzVSw57kUUEJs0R+4kL3c+jlOdszyvKrz4ovBl1sNN9sBDt8PaEICjNnh6JH83Xd77S78VtK7GrWBuUjDSxBgM2H0fcXJA/JVCChgbQnKlSNeTbe+4xX3bME9HhQlzrBhPKpGBtWJPgLwUvZ1uKwQY3JZcga4euRmeAjnnKE=", privateKey));
    }

}

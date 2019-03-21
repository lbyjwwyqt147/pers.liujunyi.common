package pers.liujunyi.cloud.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;
import java.util.TreeMap;

/***
 *
 * @FileName: CsoftSecurityUtil
 * @Company:
 * @author    ljy
 * @Date      2019年03月21日
 * @version   1.0.0
 * @remark:   通用加密工具 RSA+ASE+SHA256(非对称加密（对称秘钥），对称加密数据，Sha256消息摘要，RSA签名)
 * @explain
 *
 *
 */
@Slf4j
public class CsoftSecurityUtil {


    /**  初始化私钥  通过调用 CsoftSecurityUtil.createKeyPairs 获取 */
    public static final  String PRIVATE_KEY_STR = "" ;

    /** 加密数据和秘钥的编码方式 */
    public static final String UTF_8 = "UTF-8";

    /** 填充方式 */
    public static final String AES_ALGORITHM = "AES/CFB/PKCS5Padding";
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";


    /**
     *  解密接收数据
     *  @param externalPublicKey   公密
     *  @param selfPrivateKey      私密
     *  @param receiveData        要解密的数据
     *  @throws InvalidKeyException
     *  @throws NoSuchPaddingException
     *  @throws NoSuchAlgorithmException
     *  @throws BadPaddingException
     *  @throws IllegalBlockSizeException
     *  @throws UnsupportedEncodingException
     *  @throws InvalidAlgorithmParameterException
     *  @throws DecoderException
     */
    public static String decryptReceivedData(PublicKey externalPublicKey, PrivateKey selfPrivateKey, String receiveData) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException, DecoderException {
        @SuppressWarnings("unchecked")
        Map<String, String> receivedMap = (Map<String, String>) JSON.parse(receiveData);
        // receivedMap为请求方通过from urlencoded方式，请求过来的参数列表
        String inputSign = receivedMap.get("sign");
        // 用请求方提供的公钥验签，能配对sign，说明来源正确
        inputSign = decryptRSA(externalPublicKey, inputSign);
        // 校验sign是否一致
        String sign = sha256(receivedMap);
        if (!sign.equals(inputSign)) {
            // sign校验不通过，说明双方发送出的数据和对方收到的数据不一致
            log.info("input sign: " + inputSign + ", calculated sign: " + sign);
            return null;
        }
        // 解密请求方在发送请求时，加密data字段所用的对称加密密钥
        String key = receivedMap.get("key");
        String salt = receivedMap.get("salt");
        key = decryptRSA(selfPrivateKey, key);
        salt = decryptRSA(selfPrivateKey, salt);
        // 解密data数据
        String data = decryptAES(key, salt, receivedMap.get("data"));
        log.info("接收到的data内容 " + receiveData + " 解密后：" + data);
        return data;
    }

    /**
     *   加密数据组织示例
     *  @param externalPublicKey
     *  @param selfPrivateKey
     *  @return sendData 加密后的待发送数据
     *  @throws NoSuchAlgorithmException
     *  @throws InvalidKeySpecException
     *  @throws InvalidKeyException
     *  @throws NoSuchPaddingException
     *  @throws UnsupportedEncodingException
     *  @throws BadPaddingException
     *  @throws IllegalBlockSizeException
     *  @throws InvalidAlgorithmParameterException
     */
    public static String encryptSendData(PublicKey externalPublicKey, PrivateKey selfPrivateKey, JSONObject sendData) throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, NoSuchPaddingException, UnsupportedEncodingException, BadPaddingException,
            IllegalBlockSizeException, InvalidAlgorithmParameterException {

        // 随机生成对称加密的密钥和IV (IV就是加盐的概念，加密的偏移量)
        String aesKeyWithBase64 = genRandomAesSecretKey();
        String aesIVWithBase64 = genRandomIV();

        // 用接收方提供的公钥加密key和salt，接收方会用对应的私钥解密
        String key = encryptRSA(externalPublicKey, aesKeyWithBase64);
        String salt = encryptRSA(externalPublicKey, aesIVWithBase64);

        // 组织业务数据信息，并用上面生成的对称加密的密钥和IV进行加密
        log.info("发送的待加密data内容：" + sendData.toJSONString());
        String cipherData = encryptAES(aesKeyWithBase64, aesIVWithBase64, sendData.toJSONString());

        // 组织请求的key、value对
        Map<String, String> requestMap = new TreeMap<>();
        requestMap.put("key", key);
        requestMap.put("salt", salt);
        requestMap.put("data", cipherData);
        // 添加来源标识
        requestMap.put("source", "由接收方提供");

        // 计算sign，并用请求方的私钥加签，接收方会用请求方发放的公钥验签
        String sign = sha256(requestMap);
        requestMap.put("sign", encryptRSA(selfPrivateKey, sign));

        // TODO: 以form urlencoded方式调用，参数为上面组织出来的requestMap

        // 注意：请务必以form urlencoded方式，否则base64转码后的个别字符可能会被转成空格，对方接收后将无法正常处理
        JSONObject json = new JSONObject();
        json.putAll(requestMap);
        return json.toString();
    }

    /**
     *  获取随机的对称加密的密钥
     *  @return  对称秘钥字符
     *  @throws NoSuchAlgorithmException
     *  @throws UnsupportedEncodingException
     *  @throws IllegalBlockSizeException
     *  @throws BadPaddingException
     *  @throws InvalidKeyException
     *  @throws NoSuchPaddingException
     */
    private static String genRandomAesSecretKey() throws NoSuchAlgorithmException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchPaddingException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        String keyWithBase64 = Base64.encodeBase64(secretKey.getEncoded()).toString();
        return keyWithBase64;

    }

    private static String genRandomIV() {
        SecureRandom r = new SecureRandom();
        byte[] iv = new byte[16];
        r.nextBytes(iv);
        String ivParam = Base64.encodeBase64(iv)+"";
        return ivParam;
    }

    /**
     * 对称加密数据
     *
     * @param keyWithBase64
     * @param ivWithBase64
     * @param plainText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     */
    private static String encryptAES(String keyWithBase64, String ivWithBase64, String plainText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        byte[] keyWithBase64Arry = keyWithBase64.getBytes();
        byte[] ivWithBase64Arry = ivWithBase64.getBytes();
        SecretKeySpec key = new SecretKeySpec(Base64.decodeBase64(keyWithBase64Arry), "AES");
        IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(ivWithBase64Arry));
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return Base64.encodeBase64(cipher.doFinal(plainText.getBytes(UTF_8))).toString();
    }

    /**
     * 对称解密数据
     *
     * @param keyWithBase64
     * @param cipherText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     */
    private static String decryptAES(String keyWithBase64, String ivWithBase64, String cipherText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        byte[] keyWithBase64Arry = keyWithBase64.getBytes();
        byte[] ivWithBase64Arry = ivWithBase64.getBytes();
        byte[] cipherTextArry = cipherText.getBytes();
        SecretKeySpec key = new SecretKeySpec(Base64.decodeBase64(keyWithBase64Arry), "AES");
        IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(ivWithBase64Arry));

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return new String(cipher.doFinal(Base64.decodeBase64(cipherTextArry)), UTF_8);
    }

    /**
     * 非对称加密，根据公钥和原始内容产生加密内容
     *
     * @param key
     * @param plainText
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     */
    private static String encryptRSA(Key key, String plainText)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64(cipher.doFinal(plainText.getBytes(UTF_8))).toString();
    }

    /**
     * 根据私钥和加密内容产生原始内容
     * @param key
     * @param content
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws DecoderException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     */
    private static String decryptRSA(Key key, String content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, DecoderException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] contentArry = content.getBytes();
        return new String(cipher.doFinal(Base64.decodeBase64(contentArry)), UTF_8);
    }

    /**
     * 计算sha256值
     *
     * @param paramMap
     * @return 签名后的所有数据，原始数据+签名
     */
    private static String sha256(Map<String, String> paramMap) {
        Map<String, String> params = new TreeMap<>(paramMap);
        StringBuilder concatStr = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if ("sign".equals(entry.getKey())) {
                continue;
            }
            concatStr.append(entry.getKey() + "=" + entry.getValue() + "&");
        }

        return DigestUtils.md5Hex(concatStr.toString());
    }

    /**
     * 创建RSA的公钥和私钥示例 将生成的公钥和私钥用Base64编码后打印出来
     * @throws NoSuchAlgorithmException
     */
    public static void createKeyPairs() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        log.info("公钥:" + Base64.encodeBase64(publicKey.getEncoded()));
        log.info("私钥:" + Base64.encodeBase64(privateKey.getEncoded()));
    }

    /**
     *  默认的RSA解密方法 一般用来解密 参数 小数据
     *  @param privateKeyStr
     *  @param data
     *  @return
     *  @throws NoSuchAlgorithmException
     *  @throws InvalidKeySpecException
     *  @throws NoSuchPaddingException
     *  @throws InvalidKeyException
     *  @throws IllegalBlockSizeException
     *  @throws BadPaddingException
     */
    public static String decryptRSADefault(String privateKeyStr,String data) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        byte[] privateKeyArray = privateKeyStr.getBytes();
        byte[] dataArray = data.getBytes();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyArray));
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.decodeBase64(dataArray)), UTF_8);
    }




}

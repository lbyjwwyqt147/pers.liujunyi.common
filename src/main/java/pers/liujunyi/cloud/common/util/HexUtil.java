package pers.liujunyi.cloud.common.util;

import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/***
 * 16进制 转换工具
 * @author ljy
 */
@Log4j2
public class HexUtil {
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * 16进制转byte数组
     * @param data 16进制字符串
     * @return byte数组
     * @throws Exception 转化失败的异常
     */
    public static byte[] hex2Bytes(final String data) throws Exception {
        final int len = data.length();

        if ((len & 0x01) != 0) {
            throw new Exception("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data.charAt(j), j) << 4;
            j++;
            f = f | toDigit(data.charAt(j), j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data) {
        return bytes2Hex(data, true);
    }

    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toLowerCase 是否小写
     * @return 转化结果
     */
    public static String bytes2Hex(final byte[] data, final boolean toLowerCase) {
        return bytes2Hex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    /**
     * bytes数组转16进制String
     * @param data bytes数组
     * @param toDigits DIGITS_LOWER或DIGITS_UPPER
     * @return 转化结果
     */
    private static String bytes2Hex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return new String(out);
    }
    /**
     * 16转化为数字
     * @param ch 16进制
     * @param index 索引
     * @return 转化结果
     * @throws Exception 转化失败异常
     */
    private static int toDigit(final char ch, final int index)
            throws Exception {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new Exception("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

    /**
     * 16进制字符串转字符串
     * @param hex
     * @return
     * @throws Exception
     */
    public static String hex2String(String hex) throws Exception{
        String r = bytes2String(hexString2Bytes(hex));
        return r;
    }

    /**
     * 字节数组转字符串
     * @param b
     * @return
     * @throws Exception
     */
    public static String bytes2String(byte[] b) throws Exception {
        String r = new String (b,"UTF-8");
        return r;
    }

    /**
     * 16进制字符串转字节数组
     * @param hex
     * @return
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

    /**
     * 字符转换为字节
     * @param c
     * @return
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 字符串转16进制字符串
     * @param s
     * @return
     * @throws Exception
     */
    public static String string2HexString(String s) throws Exception{
        String r = bytes2HexString(string2Bytes(s));
        return r;
    }

    /**
     * 字节数组转16进制字符串
     * @param b
     * @return
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /**
     * 字符串转字节数组
     * @param s
     * @return
     */
    public static byte[] string2Bytes(String s){
        byte[] r = s.getBytes();
        return r;
    }

    /**
     * 对象转Byte数组
     * @param obj
     * @return
     */
    public static byte[] objectToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            System.out.println("objectToByteArray failed, " + e);
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    log.info("close objectOutputStream failed, " + e);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    log.info("close byteArrayOutputStream failed, " + e);
                }
            }

        }
        return bytes;
    }

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
        }
        return data;
    }

    /**
     * 用1字节就可以表示b
     * @param b
     * @return
     */
    public static String numToHex8(int b) {
        //2表示需要两个16进行数
        return String.format("%02X", b);
    }

    /**
     *  需要使用2字节表示b
     * @param b
     * @return
     */
    public static String numToHex16(int b) {
        return String.format("%04X", b);
    }

    /**
     * 需要使用4字节表示b
     * @param b
     * @return
     */
    public static String numToHex32(int b) {
        return String.format("%08X", b);
    }

    /**
     * 字节数组到long的转换.
     * @param b
     * @return
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        // 最低位
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        // 最低位
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * byte转long
     * @param byteNum
     * @return
     */
    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    /**
     * int转byte
     * @param value
     * @return
     */
    public static byte[] intToBytes( int value ) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte 转 int
     * @param bArr
     * @return
     */
    public static int ByteArrayToInt(byte[] bArr) {
        if (bArr.length != 4) {
            return -1;
        }
        return (int) ((((bArr[3] & 0xff) << 24)
                | ((bArr[2] & 0xff) << 16)
                | ((bArr[1] & 0xff) << 8)
                | ((bArr[0] & 0xff) << 0)));
    }

    /**
     *
     * @param src
     * @return
     */
    public static byte[] toByteArray(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i] >> 8);
            dest[i * 2 + 1] = (byte) (src[i] >> 0);
        }
        return dest;
    }

    /**
     * short 转为 byte 数组
     * @param s
     * @return
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }
}


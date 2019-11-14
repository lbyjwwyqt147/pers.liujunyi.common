package pers.liujunyi.cloud.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

/***
 * 图片转Base64 工具类
 *
 * @author ljy
 */
public class Base64Convert {

    /**
     * image 转为 Base64
     * @return 图片 Base64 码
     * @throws IOException
     */
    public static String imageToBase64(InputStream in) throws IOException {
        String  imageBase64 = null;
        try {
            // in.available()返回文件的字节长度
            byte[] bytes = new byte[in.available()];
            // 将文件中的内容读入到数组中
            in.read(bytes);
            //将字节流数组转换为字符串
            imageBase64 = new BASE64Encoder().encode(bytes);
            in.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return imageBase64;
    }

    /**
     * 将base64解码成图片并保存在传入的路径下
     * 第一个参数为base64 ，第二个参数为路径
     *
     * @param base64, imgFilePath
     * @return boolean
     */
    public static boolean base64ToImage(String base64, String imgFilePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        // 图像数据为空
        if (base64 == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(base64);
            for (int i = 0; i < b.length; ++i) {
                // 调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 输出图片到前端
     * @param is
     * @param response
     */
    public static void imageOutput(InputStream is, HttpServletResponse response) {
        try {
            if (is == null) {
                return;
            }
            response.setContentType("image/png");

            BufferedImage image = ImageIO.read(is);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "png", out);

            is.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

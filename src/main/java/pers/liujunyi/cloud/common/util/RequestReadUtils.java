package pers.liujunyi.cloud.common.util;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/***
 * 文件名称: RequestReadUtils
 * 文件描述: request的body数据
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2020/4/15 16:18
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public class RequestReadUtils {

    private static final int BUFFER_SIZE = 1024 * 8;

    public static String read(HttpServletRequest request)  {
        String body = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = request.getReader();
            StringWriter writer = new StringWriter();
            write(bufferedReader,writer);
            body = writer.getBuffer().toString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return body;
    }

    public static long write(Reader reader, Writer writer) throws IOException {
        return write(reader, writer, BUFFER_SIZE);
    }

    public static long write(Reader reader, Writer writer, int bufferSize) throws IOException {
        int read;
        long total = 0;
        char[] buf = new char[bufferSize];
        while( ( read = reader.read(buf) ) != -1 ) {
            writer.write(buf, 0, read);
            total += read;
        }
        return total;
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
}

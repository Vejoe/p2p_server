package com.zhihao.p2p_server.Utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class Base64Utils {
    /**
     * @author weizhihao
     * @param imgFile 图片路径
     * @return
     */
    public static String ImageToBase64ByLocal(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     *
     * @param imgStr 图片字节数组字符串
     * @param imgFilePath Base64解码并生成图片并存放路径。
     * @return
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片

        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
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
     * 对字节数组字符串进行Base64解码并生成图片
     * @param imgStr 图片字符串
     * @return byte[]
     */
    public static byte[] getStrToBytes(String imgStr) {
        if (imgStr == null) // 图像数据为空
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param base64string
     * @return InputStream
     */
    public static InputStream BaseToInputStream(String base64string){

        ByteArrayInputStream stream = null;

        try {

            BASE64Decoder decoder = new BASE64Decoder();

            byte[] bytes1 = decoder.decodeBuffer(base64string);

            stream = new ByteArrayInputStream(bytes1);

        } catch (Exception e) {

    // TODO: handle exception

        }

        return stream;

    }
}

package com.zhihao.p2p_server.Utils;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageUtils {
    /**
     * 改变图片的大小到宽为size，然后高随着宽等比例变化
     * @param ImageBig 大的
     * @throws IOException
     */
    public static String BigImageToSmallImage(String ImageBig) throws IOException {
        InputStream is = Base64Utils.BaseToInputStream(ImageBig);
        BufferedImage prevImage = ImageIO.read(is);
        double width = 200;
        double height = 200;
        int newWidth = (int)(width );
        int newHeight = (int)(height );
        BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String newSmallImage = encoder.encode(outputStream.toByteArray());
        is.close();
        outputStream.close();
        return newSmallImage;
    }
}

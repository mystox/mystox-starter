package com.kongtrolink.framework.scloud.util;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Mg on 2018/5/18.
 */
public class ImageUtil {

    static BASE64Encoder encoder = new BASE64Encoder();
    static BASE64Decoder decoder = new BASE64Decoder();

    public static byte[] getImageBinary(String ImagePath){
        File file = new File(ImagePath);
        return getImageBinary(file);
    }

    public static byte[] getImageBinary(File file){
        int width = 400, height = 400;
        return getImageBinaryWH(file, width, height);
    }

    /**
     * 功能描述:根据指定压缩比获取图片字节数组
     */
    public static byte[] getImageBinaryWH(File file, int width, int height){
        BufferedImage bi;
        ByteArrayOutputStream outputStream = null;
        try {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//            System.out.println("该文件的后缀是:"+suffix);
            /*
             * size(width,height) 若图片横比400小，高比400小，不变
             * 若图片横比400小，高比400大，高缩小到400，图片比例不变 若图片横比400大，高比400小，横缩小到400，图片比例不变
             * 若图片横比400大，高比400大，图片按比例缩小，横为400或高为400
             */
            if(!"png".equals(suffix)){
                file = converter(file,file);
            }
            outputStream = new ByteArrayOutputStream();
            if(-1 == width && -1 == height){
                ImageIO.write(ImageIO.read(file),"png", outputStream);
            }else {
                bi = Thumbnails.of(file).size(width, height).asBufferedImage();//进行图片压缩
                ImageIO.write(bi,"png", outputStream);
            }
            byte[] bytes = outputStream.toByteArray();
//            return encoder.encodeBuffer(bytes).trim();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] getFileBinary(MultipartFile file){
        InputStream inputStream = null;
        try {
            inputStream  = file.getInputStream();
            return inputStreamToByte(inputStream);//将文件保存到字节数组中
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void base64StringToImage(String savePath,String base64String){
        try {
            byte[] bytes1 = decoder.decodeBuffer(base64String);
            ByteArrayInputStream out = new ByteArrayInputStream(bytes1);
            BufferedImage bi1 =ImageIO.read(out);
            File w2 = new File(savePath);//可以是jpg,png,gif格式
            ImageIO.write(bi1, "png", w2);//不管输出什么格式图片，此处不需改动
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File converter(File imgFile,File formatFile) throws IOException{
        imgFile.canRead();
        BufferedImage bi = ImageIO.read(imgFile);
        BufferedImage newBufferedImage = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
        ImageIO.write(newBufferedImage, "png", formatFile);
        return formatFile;
    }

    //将文件保存到字节数组中
    private static byte [] inputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bAOutputStream = new ByteArrayOutputStream();
        int ch;
        while((ch = is.read() ) != -1){
            bAOutputStream.write(ch);
        }
        byte data [] =bAOutputStream.toByteArray();
        bAOutputStream.close();
        return data;
    }
}

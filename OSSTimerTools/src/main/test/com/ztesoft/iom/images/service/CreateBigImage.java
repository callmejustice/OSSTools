package com.ztesoft.iom.images.service;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @author: huang.jing
 * @Date: 2018/3/13 0013 - 11:08
 */
public class CreateBigImage {
    public static void main(String[] args) {
////        System.out.println("123");
////
////        //设置图片宽度相同
//        changeImage("D:/", "13687881613_771-20170418-001620_constructType_20180315154508125.jpg", "13687881613_771-20170418-001620_constructType_20180315154508125_new.jpg", 500, 466);
//        changeImage("D:/", "13687881613_771-20170418-001620_custSign_20180315154507992.jpg", "13687881613_771-20170418-001620_custSign_20180315154507992_new.jpg", 500, 466);
////        changeImage("D:/imgs/", "3.jpg", "3.jpg", 300,200);
//        //获取宽度相同的图片
//        String img1 = "D:/13687881613_771-20170418-001620_constructType_20180315154508125_new.jpg";
//        String img2 = "D:/13687881613_771-20170418-001620_custSign_20180315154507992_new.jpg";
//        String[] imgs = new String[]{img1, img2};
////        //图片拼接
//        mergeWidth(imgs, "jpg", "D:/sign_big.jpg");
//
        String img3 = "D:/13607710116_771-20180315-082600_bottomPic_20180316170749260.jpg";
        String img4 = "D:/13607710116_771-20180315-082600_custSign_20180316170748879.jpg";
        String img5 = "D:/13607710116_771-20180315-082600_constructType_20180316170749051.jpg";
        String[] imgs2 = new String[]{img3, img4, img5};
        mergeHeight(imgs2, "jpg", "D:/sign.jpg");
////        String folderPath = "D:/imgs";
////        changeFolderImages(folderPath,600,400);
//
////        mergeFolderImgs(folderPath,"jpg","D:/imgs/merge.jpg");


//        String pattern = "[*]";
//        Pattern r = Pattern.compile(pattern);
//        String content = "/iom_mobile/app/pic//2018/3/16/23/134****2989_771-20180314-082556_custSign_20180316110151940.jpg";
//        Matcher m = r.matcher(content);
//        boolean isMatch = m.find();
//        System.out.println(isMatch);
//        content = content.replaceAll("\\*", "@");
//        System.out.println(content);
    }

    /**
     * 设置图片大小（单张图片）
     *
     * @param path      路径
     * @param oldimg    旧图片名称
     * @param newimg    新图片名称
     * @param newWidth  新图片宽度
     * @param newHeight 新图片高度
     */
    public static void changeImage(String path, String oldimg, String newimg, int newWidth, int newHeight) {
        try {
            File file = new File(path + oldimg);
            Image img = ImageIO.read(file);
            // 构造Image对象
//               int wideth = img.getWidth(null); // 得到源图宽
//               int height = img.getHeight(null); // 得到源图长
            BufferedImage tag = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_RGB);
            tag.getGraphics()
                    .drawImage(img, 0, 0, newWidth, newHeight, null); // 绘制后的图
            ImageIO.write(tag, newimg.split("\\.")[1], new File(path + newimg));
//            FileOutputStream out = new FileOutputStream(path + newimg);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(tag); // 近JPEG编码
//            out.close();
        } catch (IOException e) {
            System.out.println("处理文件出现异常");
            e.printStackTrace();
        }
    }

    /**
     * Java纵向拼接多张图片
     *
     * @param pics:图片源文件 （必须要宽度一样），如：
     *                   String img1 = "D:/imgs/3.jpg";
     *                   String img2 = "D:/imgs/3.jpg";
     *                   String img3 = "D:/imgs/big.jpg";
     *                   String[] pics = new String[]{img1,img2,img3};
     * @param type       ：图片输出类型（jpg，png，jpeg...）
     * @param dst_pic    ：图片输出绝对路径，如 String dst_pic="D:/imgs/big2.jpg";
     * @return
     */
    public static boolean mergeHeight(String[] pics, String type, String dst_pic) {

        int len = pics.length;  //图片文件个数
        if (len < 1) {
            System.out.println("pics len < 1");
            return false;
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(pics[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];// 从图片中读取RGB
            ImageArrays[i] = images[i].getRGB(0, 0, width, height,
                    ImageArrays[i], 0, width);
        }

        int dst_height = 0;
        int dst_width = images[0].getWidth();
        for (int i = 0; i < images.length; i++) {
            dst_width = dst_width > images[i].getWidth() ? dst_width
                    : images[i].getWidth();

            dst_height += images[i].getHeight();
        }
        System.out.println(dst_width);
        System.out.println(dst_height);
        if (dst_height < 1) {
            System.out.println("dst_height < 1");
            return false;
        }

        // 生成新图片
        try {
            // dst_width = images[0].getWidth();
            BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,
                    BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            for (int i = 0; i < images.length; i++) {
                ImageNew.setRGB(0, height_i, dst_width, images[i].getHeight(),
                        ImageArrays[i], 0, dst_width);
                height_i += images[i].getHeight();
            }

            File outFile = new File(dst_pic);
            ImageIO.write(ImageNew, type, outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Java横向拼接多张图片
     *
     * @param pics:图片源文件 （必须要宽度一样），如：
     *                   String img1 = "D:/imgs/3.jpg";
     *                   String img2 = "D:/imgs/3.jpg";
     *                   String img3 = "D:/imgs/big.jpg";
     *                   String[] pics = new String[]{img1,img2,img3};
     * @param type       ：图片输出类型（jpg，png，jpeg...）
     * @param dst_pic    ：图片输出绝对路径，如 String dst_pic="D:/imgs/big2.jpg";
     * @return
     */
    public static boolean mergeWidth(String[] pics, String type, String dst_pic) {

        int len = pics.length;  //图片文件个数
        if (len < 1) {
            System.out.println("pics len < 1");
            return false;
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(pics[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];// 从图片中读取RGB
            ImageArrays[i] = images[i].getRGB(0, 0, width, height,
                    ImageArrays[i], 0, width);
        }

        int dst_height = images[0].getHeight();
        int dst_width = 0;
        for (int i = 0; i < images.length; i++) {
            dst_height = dst_height > images[i].getHeight() ? dst_height : images[i].getHeight();

            dst_width += images[i].getWidth();
        }
        System.out.println(dst_width);
        System.out.println(dst_height);
        if (dst_height < 1 || dst_width < 1) {
            System.out.println("dst_height < 1 or dst_width < 1");
            return false;
        }

        // 生成新图片
        try {
            // dst_width = images[0].getWidth();
            BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,
                    BufferedImage.TYPE_INT_RGB);
            int width_i = 0;
            for (int i = 0; i < ImageArrays.length; i++) {
                ImageNew.setRGB(width_i, 0, images[i].getWidth(), dst_height, ImageArrays[i], 0, images[i].getWidth());
                width_i += images[i].getWidth();
            }

            File outFile = new File(dst_pic);
            ImageIO.write(ImageNew, type, outFile);// 写图片
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

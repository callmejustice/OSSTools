package com.ztesoft.iom.order.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Description: 图片合并业务类
 * @author: huang.jing
 * @Date: 2018/3/15 0015 - 16:09
 */
public class CreateBigImage {

    private static Logger log = LogManager.getLogger(CreateBigImage.class);

    /**
     * 设置图片大小（单张图片）
     * @param is
     * @param newimg
     * @param newWidth
     * @param newHeight
     */
    public static void changeImage(InputStream is, String newimg, int newWidth, int newHeight) {
        try {
            Image img = ImageIO.read(is);
            // 构造Image对象
            BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(img, 0, 0, newWidth, newHeight, null); // 绘制后的图片
            File file = new File(newimg);
            ImageIO.write(tag, newimg.split("\\.")[1], file);
        } catch (IOException e) {
            log.error("设置图片大小异常", e);
        }
    }

    /**
     * Java纵向拼接多张图片
     *
     * @param pics    图片源文件 （必须要宽度一样），如：
     *                   String img1 = "D:/imgs/3.jpg";
     *                   String img2 = "D:/imgs/3.jpg";
     *                   String img3 = "D:/imgs/big.jpg";
     *                   ArrayList pics = new ArrayList();
     *                   pics.add(img1);
     *                   pics.add(img2);
     *                   pics.add(img3);
     * @param type    图片输出类型（jpg，png，jpeg...）
     * @param dst_pic    图片输出绝对路径，如 String dst_pic="D:/imgs/big2.jpg";
     * @return
     */
    public static boolean mergeHeight(ArrayList pics, String type, String dst_pic) {

        int len = pics.size();  //图片文件个数
        if (len < 1) {
            log.error("待纵向拼接文件数量小于1");
            return false;
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File((String) pics.get(i));
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                log.error("读取图片文件异常", e);
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

        log.debug("拼接后文件宽高为：" + dst_width + "X" + dst_height);
        if (dst_height < 1 || dst_width < 1) {
            log.error("文件高度或者宽度小于1");
            return false;
        }

        // 生成新图片
        try {
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
            log.error("写图片文件异常", e);
            return false;
        }
        return true;
    }

    /**
     * Java横向拼接多张图片
     *
     * @param pics    图片源文件 （必须要高度一样），如：
     *                   String img1 = "D:/imgs/3.jpg";
     *                   String img2 = "D:/imgs/3.jpg";
     *                   String img3 = "D:/imgs/big.jpg";
     *                   ArrayList pics = new ArrayList();
     *                   pics.add(img1);
     *                   pics.add(img2);
     *                   pics.add(img3);
     * @param type    图片输出类型（jpg，png，jpeg...）
     * @param dst_pic    图片输出绝对路径，如 String dst_pic="D:/imgs/big2.jpg";
     * @return
     */
    public static boolean mergeWidth(ArrayList pics, String type, String dst_pic) {

        int len = pics.size();  //图片文件个数
        if (len < 1) {
            log.error("待横向拼接文件数量小于1");
            return false;
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File((String) pics.get(i));
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                log.error("读取图片文件异常", e);
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

        log.debug("拼接后文件宽高为：" + dst_width + "X" + dst_height);
        if (dst_height < 1 || dst_width < 1) {
            log.error("文件高度或者宽度小于1");
            return false;
        }

        // 生成新图片
        try {
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
            log.error("写图片文件异常", e);
            return false;
        }
        return true;
    }

}

package com.ztesoft.iom.manage.file.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 文件处理类
 * @author: huang.jing
 * @Date: 2018/3/27 0027 - 15:51
 */
public class FileUtils {

    private static Logger log = LogManager.getLogger(FileUtils.class);

    public static void main(String[] args) {
        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 读入TXT文件 */
            String pathname = "D:\\newBuild_20180410.AVL"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "GBK"); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = br.readLine();
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                if(line != null) {
                    String[] lineArr = line.split("\\|");
                    for (int i = 0; i < lineArr.length; i++) {
                        String item = lineArr[i];
                        if(i == lineArr.length - 1) {
                            String[] questionArr = item.split("\\$");
                            for (int j = 0; j < questionArr.length; j++) {
                                String question = questionArr[j];
                                System.out.println("question：" + question);
                            }
                        } else {
                            System.out.println("item：" + item);
                        }
                    }
                }
                System.out.println("--------------");
            }

        } catch (Exception e) {
            System.out.println("读文件异常");
            e.printStackTrace();
        }

//        FileUtils fileUtils = new FileUtils();
//        JSONObject chkContentJson = fileUtils.loadFile("D:\\\\20180201.CHK");
//        if(chkContentJson.getBoolean("loadFlag")) {
//            List chkContentList = chkContentJson.getObject("fileContentList", ArrayList.class);
//            if(chkContentList.size() >= 2) {
//                String avlFileName = (String) chkContentList.get(0);
//                int avlFileLength = Integer.parseInt((String) chkContentList.get(1));
//
//                JSONObject avlContentJson = fileUtils.loadFile("D:\\\\" + avlFileName);
//
//                List avlContentList = avlContentJson.getObject("fileContentList", ArrayList.class);
//                if(avlContentList.size() == avlFileLength) {
//                    System.out.println("CHK文件中文件行数与AVL文件内容行数匹配，校验通过……");
//                } else {
//                    System.out.println("CHK文件中文件行数与AVL文件内容行数不匹配，校验不通过……");
//                }
//            } else {
//                System.out.println("CHK文件行数不足2行，不符合校验规则……");
//            }
//        }
    }

    /**
     * 读取文件
     * @param filePath
     * @return
     */
    public JSONObject loadFile(String filePath) {
        JSONObject returnJson = new JSONObject();
        boolean loadFlag = false;

        try {
            List fileContentList = new ArrayList();
            File chkFile = new File(filePath); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(new FileInputStream(chkFile), "GBK"); // 建立一个输入流对象reader
            System.out.println("开始读取文件内容……");
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            // 第一行数据
            String line = br.readLine();
            while (line != null) {
                fileContentList.add(line);
                line = br.readLine(); // 一次读入一行数据
            }

            returnJson.put("fileContentList", fileContentList);
            loadFlag = true;
            System.out.println("读取文件内容结束……");
        } catch (Exception e) {
            System.out.println("读取文件异常");
            e.printStackTrace();
        } finally {
            returnJson.put("loadFlag", loadFlag);
        }

        return returnJson;
    }
}

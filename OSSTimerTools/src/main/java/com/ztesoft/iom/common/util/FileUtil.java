package com.ztesoft.iom.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 文件处理类
 * @author: huang.jing
 * @Date: 2018/4/2 0002 - 16:28
 */
public class FileUtil {

    private static Logger log = LogManager.getLogger(FileUtil.class);

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
            log.info("读取文件内容结束……");
        } catch (Exception e) {
            log.error("读取文件异常", e);
        } finally {
            returnJson.put("loadFlag", loadFlag);
        }

        return returnJson;
    }

}

package com.ztesoft.iom.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @Description: 参数类
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 16:54
 */
public class ParamConfig {

    private static Logger log = LogManager.getLogger(ParamConfig.class);
    private Properties params = new Properties();
    private static String FILENAME = "iom.properties";
    private static ParamConfig paramsConfig = null;

    public ParamConfig(){
    }

    /**
     * 获取实例对象
     * @return
     */
    public static ParamConfig getInstance(){
        if(paramsConfig == null){
            paramsConfig = new ParamConfig();
        }
        return paramsConfig;
    }

    /**
     * 初始化参数，在系统初始化时载入。或者定时任务刷新。
     * @param path
     */
    public void initParams(String path){

        try {
            File cfgFile = cfgFile = new File(path += FILENAME);
            FileInputStream fin = new FileInputStream(cfgFile);
            if(params != null){
                params = new Properties();
            }
            // 指定输入流的编码，避免中文乱码
            params.load(new InputStreamReader(fin, "utf-8"));
            fin.close();

            Enumeration enu = params.keys();
            String key = "";
            String val = "";
            while(enu.hasMoreElements()){
                key = (String)enu.nextElement();
                val = params.getProperty(key);
                if(val!=null && !"".equals(val)){
                    params.put(key, val.trim());
                }
            }
            log.info("静态参数从[" + FILENAME + "]获取成功");
        } catch (FileNotFoundException e) {
            log.error("文件不存在: " + path + "  " + e);
            return;
        } catch (IOException e) {
            log.error("初始化文件失败： " + path + "  " + e);
            return;
        }

    }

    /**
     * 获取参数值
     * @param name 参数名称
     * @return
     */
    public String getParamValue(String name){
        return params.getProperty(name);
    }
}

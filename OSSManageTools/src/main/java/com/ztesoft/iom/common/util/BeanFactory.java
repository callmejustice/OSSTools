package com.ztesoft.iom.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description: 工厂类
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 16:53
 */
public class BeanFactory implements ApplicationContextAware {

    private static Logger log = LogManager.getLogger(BeanFactory.class);
    static ApplicationContext context = null;

    static {

        try{
            log.info("初始化BeanFactory成功……");
        }catch(Exception e){
            log.error("初始化BeanFactory异常", e);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

}

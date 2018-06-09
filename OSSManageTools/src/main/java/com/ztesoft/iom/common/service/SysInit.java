package com.ztesoft.iom.common.service;

import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.manage.param.service.ParamMappingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @Description: 系统初始化类
 * @author: huang.jing
 * @Date: 2017/12/28 0028 - 16:52
 */
public class SysInit extends HttpServlet {

    private static Logger log = LogManager.getLogger(SysInit.class);

    public void init() throws ServletException {
        try {
            //获取静态数据
            String fileSeparator = System.getProperty("file.separator");
            String webInfPath = getServletContext().getRealPath("/") + fileSeparator + "WEB-INF/classes" + fileSeparator;
            ParamConfig.getInstance().initParams(webInfPath);

            ParamMappingService paramMappingService = (ParamMappingService) BeanFactory.getApplicationContext().getBean("paramMappingService");
            paramMappingService.loadParamMappingObject();
        } catch (Exception e) {
            log.error("系统初始化异常", e);
        }
    }
}

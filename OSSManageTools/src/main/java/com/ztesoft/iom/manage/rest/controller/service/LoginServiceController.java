package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ParamConfig;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.manage.rest.dao.UserDAO;
import com.ztesoft.iom.manage.svn.service.SVNLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Description: 登录控制器
 * @author: huang.jing
 * @Date: 2017/12/31 0031 - 17:38
 */
@RestController
@RequestMapping("/controller/service/login")
public class LoginServiceController {

    private static Logger log = LogManager.getLogger(LoginServiceController.class);

    /**
     * 登录方法
     *
     * @param httpSession
     * @param logonJson
     * @return
     */
    @RequestMapping(value = "/logon.do", method = RequestMethod.POST)
    public @ResponseBody
    Response logon(HttpSession httpSession, @RequestBody JSONObject logonJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 使用svn用户名登录
        if ("true".equals(ParamConfig.getInstance().getParamValue("svnLogin"))) {
            SVNLogService svlLogService = (SVNLogService) BeanFactory.getApplicationContext().getBean("svnLogService");
            if (svlLogService.doSvnLogin(logonJson)) {
                // 将登录进行保存在json对象后放入缓存
                JSONObject ossLoginUser = new JSONObject();
                ossLoginUser.put("userName", logonJson.getString("userName"));
                httpSession.setAttribute("ossLoginUser", ossLoginUser);
                response.success(returnData);
            } else {
                response.failure("用户名或者密码输入错误");
            }
        } if ("true".equals(ParamConfig.getInstance().getParamValue("dbLogin"))) {
            UserDAO userDAO = (UserDAO) BeanFactory.getApplicationContext().getBean("userDAOImpl");
            Map<String, Object> user = userDAO.queryUserByUsername(logonJson.getString("userName"));
            if(user != null && user.get("PASSWORD").equals(logonJson.getString("password"))) {
                JSONObject ossLoginUser = new JSONObject();
                ossLoginUser.put("userName", logonJson.getString("userName"));
                httpSession.setAttribute("ossLoginUser", ossLoginUser);
                response.success(returnData);
            }
        } else{
            JSONObject ossLoginUser = new JSONObject();
            ossLoginUser.put("userName", logonJson.getString("userName"));
            httpSession.setAttribute("ossLoginUser", ossLoginUser);
            response.success(returnData);
        }

        return response;
    }

    /**
     * 退出登录方法
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    public @ResponseBody
    Response logout(HttpSession httpSession) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();
        // 移除登录信息
        httpSession.removeAttribute("ossLoginUser");
        response.success(returnData);
        return response;
    }

    /**
     * 获取登录信息
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/getLoginInfo.do", method = RequestMethod.POST)
    public @ResponseBody
    Response getLoginInfo(HttpSession httpSession) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();
        if (httpSession.getAttribute("ossLoginUser") != null) {
            // 从缓存中读取登录用户名，设置为研发单的创建人
            JSONObject ossLoginUser = (JSONObject) httpSession.getAttribute("ossLoginUser");
            returnData.put("userName", ossLoginUser.getString("userName"));
        }
        response.success(returnData);
        return response;
    }

}

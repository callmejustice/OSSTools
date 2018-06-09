package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONObject;;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.WordUtils;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.manage.kbs.service.KBSManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Iterator;

/**
 * @Description: 知识库模块控制器
 * @author: huang.jing
 * @Date: 2018/3/20 0020 - 16:09
 */
@RestController
@RequestMapping("/controller/service/kbs")
public class KBSServiceController {

    private static Logger log = LogManager.getLogger(KBSServiceController.class);
    private static String serverPath = "../webapps/OSSManageTools/";

    /**
     * 创建知识点
     *
     * @param httpSession
     * @param createJson
     * @return
     */
    @RequestMapping(value = "/createKnowledge.do", method = RequestMethod.POST)
    public @ResponseBody
    Response createKnowledge(HttpSession httpSession, @RequestBody JSONObject createJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        createJson.put("author", ossLoginUser.getString("userName"));
        createJson.put("serverPath", serverPath);
        KBSManager kbsManager = (KBSManager) BeanFactory.getApplicationContext().getBean("kbsManager");

        if (kbsManager.createKnowledge(createJson)) {
            response.success(returnData);
        } else {
            response.failure("创建知识点失败");
        }

        return response;
    }

    /**
     * 上传知识内容
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadKnowledge.do", method = RequestMethod.POST)
    public @ResponseBody
    Response uploadKnowledge(HttpServletRequest request) {
        Response response = new Response();
        // 文件保存地址
        String filePath = "kbs/" + System.currentTimeMillis() + "/";
        String htmlPath = serverPath + filePath;
        // @RequestParam("file") MultipartFile file,
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            // 转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                try {
                    // 取得上传文件
                    MultipartFile multipartFile = multiRequest.getFile(iter.next());

                    // 生成临时文件
                    File tmpFile = null;
                    tmpFile = File.createTempFile("tmp", null);
                    multipartFile.transferTo(tmpFile);

                    WordUtils wordUtils = (WordUtils) BeanFactory.getApplicationContext().getBean("wordUtils");
                    String fileName = "";
                    if (multipartFile.getOriginalFilename().indexOf(".docx") > -1) {
                        fileName = wordUtils.docxToHtml(tmpFile, htmlPath);
                    } else if (multipartFile.getOriginalFilename().indexOf(".doc") > -1) {
                        fileName = wordUtils.docToHtml(tmpFile, htmlPath);
                    }

                    // 删除临时文件
                    tmpFile.deleteOnExit();
                    if (!"".equals(fileName)) {
                        JSONObject returnJson = new JSONObject();
                        returnJson.put("fileName", filePath + fileName);
                        response.success(returnJson);
                    } else {
                        response.failure("不支持上传该格式的文件");
                    }

                } catch (Exception e) {
                    log.error("读取文件异常", e);
                    response.failure(e.getMessage());
                }
            }
        }
        return response;
    }

    /**
     * 查询知识点
     *
     * @param httpSession
     * @param qryJson
     * @return
     */
    @RequestMapping(value = "/getKnowledge.do", method = RequestMethod.POST)
    public @ResponseBody
    Response getKnowledge(HttpSession httpSession, @RequestBody JSONObject qryJson) {
        log.info("getKnowledge入口……");
        Response response = new Response();

        try {
            KBSManager kbsManager = (KBSManager) BeanFactory.getApplicationContext().getBean("kbsManager");
            JSONObject returnDate = new JSONObject();
            returnDate.put("knowledgeList", kbsManager.getKnowledge(qryJson));
            response.success(returnDate);
        } catch (Exception e) {
            log.error("查询知识点异常", e);
        }
        log.info("getKnowledge出口……");
        return response;
    }

    /**
     * 删除知识点
     *
     * @param httpSession
     * @param deleteJson
     * @return
     */
    @RequestMapping(value = "/deleteKnowledge.do", method = RequestMethod.POST)
    public @ResponseBody
    Response deleteKnowledge(HttpSession httpSession, @RequestBody JSONObject deleteJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        deleteJson.put("author", ossLoginUser.getString("userName"));
        KBSManager kbsManager = (KBSManager) BeanFactory.getApplicationContext().getBean("kbsManager");

        if (kbsManager.deleteKnowledge(deleteJson)) {
            response.success(returnData);
        } else {
            response.failure("删除知识点失败");
        }

        return response;
    }

    /**
     * 修改知识点
     *
     * @param httpSession
     * @param updateJson
     * @return
     */
    @RequestMapping(value = "/editKnowledge.do", method = RequestMethod.POST)
    public @ResponseBody
    Response editKnowledge(HttpSession httpSession, @RequestBody JSONObject updateJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        updateJson.put("author", ossLoginUser.getString("userName"));
        updateJson.put("serverPath", serverPath);
        KBSManager kbsManager = (KBSManager) BeanFactory.getApplicationContext().getBean("kbsManager");

        if (kbsManager.updateKnowledge(updateJson)) {
            response.success(returnData);
        } else {
            response.failure("修改知识点失败");
        }

        return response;
    }

    /**
     * 获取缓存的登录信息
     *
     * @param httpSession
     * @return
     */
    private JSONObject getLoginSession(HttpSession httpSession) {
        JSONObject loginSessionObject = (JSONObject) httpSession.getAttribute("ossLoginUser");
        return loginSessionObject;
    }
}

package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.util.ExcelUtils;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.common.vo.TableDataResponse;
import com.ztesoft.iom.manage.requirements.service.RequirementsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: 需求模块控制器
 * @author: huang.jing
 * @Date: 2018/1/2 0002 - 10:25
 */
@RestController
@RequestMapping("/controller/service/requirements")
public class RequirementsServiceController {

    private static Logger log = LogManager.getLogger(RequirementsServiceController.class);

    /**
     * 查询需求单列表
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/qryOrderList.do", method = RequestMethod.POST)
    public @ResponseBody
    TableDataResponse qryOrderList(HttpSession httpSession
            , @RequestParam(value = "page", required = false, defaultValue = "1") String page
            , @RequestParam(value = "limit", required = false, defaultValue = "10") String limit
            , @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        TableDataResponse response = new TableDataResponse();
        JSONArray dataArray = new JSONArray();
        int code = 0;
        String msg = "";

        JSONObject dataObject = new JSONObject();
        dataObject.put("id", "gx-0001-0001");
        dataObject.put("name", "研发任务单1");
        dataObject.put("author", "huang.jing");
        dataObject.put("state", "需求分析");
        dataObject.put("createDate", "2018/1/1 15:00:00");
        dataObject.put("relaCount", "0");
        dataArray.add(dataObject);

        dataObject = new JSONObject();
        dataObject.put("id", "gx-0001-0002");
        dataObject.put("name", "研发任务单2");
        dataObject.put("author", "huang.jing1");
        dataObject.put("state", "需求开发");
        dataObject.put("createDate", "2018/1/1 16:00:00");
        dataObject.put("relaCount", "0");
        dataArray.add(dataObject);

        dataObject = new JSONObject();
        dataObject.put("id", "gx-0001-0003");
        dataObject.put("name", "研发任务单3");
        dataObject.put("author", "huang.jing1");
        dataObject.put("state", "已上线");
        dataObject.put("createDate", "2018/1/1 17:00:00");
        dataObject.put("relaCount", "1");
        dataArray.add(dataObject);

        log.info("page：" + page + ";limit：" + limit + ";keyword：" + keyword);

        response.setCode(code);
        response.setMsg(msg);
        response.setCount(dataArray.size());
        response.setData(dataArray);

        return response;
    }

    /**
     * 导入需求列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/importRequirementsExcel.do", method = RequestMethod.POST)
    public @ResponseBody
    Response importRequirementsExcel(HttpServletRequest request) {
        Response response = new Response();

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
                    ExcelUtils.checkFile(multipartFile);
                    List excelList = ExcelUtils.readExcel(multipartFile);

                    if(excelList.size() > 0) {
                        // 厂家内部看板文件中，第1第2行都是标题，ExcelUtils.readExcel只剔除了第1行，此处还要再剔除一次第1行
                        excelList.remove(0);
                        RequirementsManager requirementsManager = (RequirementsManager) BeanFactory.getApplicationContext().getBean("requirementsManager");

                        JSONObject importJson = new JSONObject();
                        importJson.put("excelList", excelList);
                        JSONObject returnJson = requirementsManager.importEOMSOrder(importJson);
                        if(returnJson.getBoolean("importFlag")) {
                            JSONObject data = new JSONObject();
                            data.put("successCount", returnJson.getString("successCount"));
                            data.put("failureCount", returnJson.getString("failureCount"));
                            data.put("errorInfo", returnJson.getString("errorInfo"));
                            response.success(data);
                        } else {
                            response.failure(returnJson.getString("errorInfo"));
                        }
                    } else {
                        response.failure("文件中没有需要导入的内容");
                    }
                } catch (Exception e) {
                    log.error("读取文件异常", e);
                    response.failure(e.getMessage());
                }
            }
        }
        return response;
    }

}

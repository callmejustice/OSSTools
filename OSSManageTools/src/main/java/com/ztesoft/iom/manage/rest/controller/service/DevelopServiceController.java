package com.ztesoft.iom.manage.rest.controller.service;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.common.vo.Response;
import com.ztesoft.iom.common.vo.TableDataResponse;
import com.ztesoft.iom.manage.develop.service.DevelopOrderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @Description: 研发模块控制器
 * @author: huang.jing
 * @Date: 2018/1/1 0001 - 20:18
 */
@RestController
@RequestMapping("/controller/service/develop")
public class DevelopServiceController {

    private static Logger log = LogManager.getLogger(DevelopServiceController.class);

    /**
     * 查询研发单列表
     * @param httpSession
     * @param page
     * @param limit
     * @param orderTitle
     * @param zmpId
     * @param orderState
     * @param version
     * @param tacheId
     * @return
     */
    @RequestMapping(value = "/qryOrderList.do", method = RequestMethod.POST)
    public @ResponseBody
    TableDataResponse qryOrderList(HttpSession httpSession
            , @RequestParam(value = "page", required = false, defaultValue = "1") String page
            , @RequestParam(value = "limit", required = false, defaultValue = "10") String limit
            , @RequestParam(value = "orderTitle", required = false, defaultValue = "") String orderTitle
            , @RequestParam(value = "zmpId", required = false, defaultValue = "") String zmpId
            , @RequestParam(value = "orderState", required = false, defaultValue = "") String orderState
            , @RequestParam(value = "version", required = false, defaultValue = "") String version
            , @RequestParam(value = "tacheId", required = false, defaultValue = "") String tacheId) {
        TableDataResponse response = new TableDataResponse();
        int code = 0;
        String msg = "";

        JSONObject qryJson = new JSONObject();
        // 从缓存中读取登录用户名，设置为研发单的创建人
        JSONObject ossLoginUser = getLoginSession(httpSession);
        qryJson.put("author", ossLoginUser.getString("userName"));
        qryJson.put("orderTitle", orderTitle);
        qryJson.put("zmpId", zmpId);
        qryJson.put("orderState", orderState);
        qryJson.put("version", version);
        qryJson.put("tacheId", tacheId);
        qryJson.put("page", page);
        qryJson.put("limit", limit);
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        JSONObject orderInfoObject = developOrderManager.getDevelopOrderList(qryJson);

        response.setCode(code);
        response.setMsg(msg);
        response.setCount(orderInfoObject.getIntValue("orderListCount"));
        response.setData(orderInfoObject.getJSONArray("orderList"));

        return response;
    }

    /**
     * 创建研发任务单
     * @param httpSession
     * @param createJson
     * @return
     */
    @RequestMapping(value = "/createOrder.do", method = RequestMethod.POST)
    public @ResponseBody
    Response createOrder(HttpSession httpSession, @RequestBody JSONObject createJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        createJson.put("author", ossLoginUser.getString("userName"));
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");

        if(developOrderManager.createOrder(createJson)) {
            response.success(returnData);
        } else {
            response.failure("创建研发任务单失败");
        }

        return response;
    }

    /**
     * 研发任务单研发模块回单
     * @param httpSession
     * @param finishOrderJson
     * @return
     */
    @RequestMapping(value = "/finishWorkOrder.do", method = RequestMethod.POST)
    public @ResponseBody
    Response finishWorkOrder(HttpSession httpSession, @RequestBody JSONObject finishOrderJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        finishOrderJson.put("author", ossLoginUser.getString("userName"));
        finishOrderJson.put("orderType", "develop");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");

        if(developOrderManager.finishWorkOrder(finishOrderJson)) {
            response.success(returnData);
        } else {
            response.failure("回单失败");
        }

        return response;
    }

    /**
     * 研发任务单研发模块退单
     * @param httpSession
     * @param finishOrderJson
     * @return
     */
    @RequestMapping(value = "/returnWorkOrder.do", method = RequestMethod.POST)
    public @ResponseBody
    Response returnWorkOrder(HttpSession httpSession, @RequestBody JSONObject finishOrderJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        finishOrderJson.put("author", ossLoginUser.getString("userName"));
        finishOrderJson.put("orderType", "develop");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");

        if(developOrderManager.returnWorkOrder(finishOrderJson)) {
            response.success(returnData);
        } else {
            response.failure("回单失败");
        }

        return response;
    }

    /**
     * 查询svn列表
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/qrySVNList.do", method = RequestMethod.POST)
    public @ResponseBody
    TableDataResponse qrySVNList(HttpSession httpSession
            , @RequestParam(value = "page", required = false, defaultValue = "1") String page
            , @RequestParam(value = "limit", required = false, defaultValue = "10") String limit
            , @RequestParam(value = "isRelaOrder", required = false, defaultValue = "") String isRelaOrder
            , @RequestParam(value = "reversion", required = false, defaultValue = "") String reversion) {
        TableDataResponse response = new TableDataResponse();
        int code = 0;
        String msg = "";

        JSONObject qryJson = new JSONObject();
        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        qryJson.put("author", ossLoginUser.getString("userName"));
        qryJson.put("page", page);
        qryJson.put("limit", limit);
        qryJson.put("isRelaOrder", isRelaOrder);
        qryJson.put("reversion", reversion);
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        JSONObject orderInfoObject = developOrderManager.getSvnLogWithChangeList(qryJson);

        response.setCode(code);
        response.setMsg(msg);
        response.setCount(orderInfoObject.getIntValue("svnLogListCount"));
        response.setData(orderInfoObject.getJSONArray("svnLogList"));

        return response;
    }

    /**
     * 提交代码
     * @param httpSession
     * @param attachJson
     * @return
     */
    @RequestMapping(value = "/attachSVN.do", method = RequestMethod.POST)
    public @ResponseBody
    Response attachSVN(HttpSession httpSession, @RequestBody JSONObject attachJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        attachJson.put("author", ossLoginUser.getString("userName"));
        attachJson.put("orderType", "develop");
        attachJson.put("nextWorkOrderOper", attachJson.getString("tester"));
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        if(developOrderManager.attachSVN(attachJson)) {
            response.success(returnData);
        } else {
            response.failure("提交代码失败");
        }

        return response;
    }

    /**
     * 转派工单
     * @param httpSession
     * @param transferJson
     * @return
     */
    @RequestMapping(value = "/transferWorkOrder.do", method = RequestMethod.POST)
    public @ResponseBody
    Response transferWorkOrder(HttpSession httpSession, @RequestBody JSONObject transferJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        transferJson.put("author", ossLoginUser.getString("userName"));
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        returnData = developOrderManager.transferWorkOrder(transferJson);
        if(returnData.getIntValue("transferCount") > 0) {
            response.success(returnData);
        } else {
            response.failure("转派失败");
        }

        return response;
    }

    /**
     * 查询任务单关联的svn列表
     * @param httpSession
     * @param qryJson
     * @return
     */
    @RequestMapping(value = "/qrySVNRelaList.do", method = RequestMethod.POST)
    public @ResponseBody
    Response qrySVNRelaList(HttpSession httpSession, @RequestBody JSONObject qryJson) {
        JSONObject returnData = new JSONObject();
        Response response = new Response();

        // 从缓存中读取登录用户名
        JSONObject ossLoginUser = getLoginSession(httpSession);
        qryJson.put("author", ossLoginUser.getString("userName"));
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        returnData = developOrderManager.getSvnLogWithChangeRelaList(qryJson);
        response.success(returnData);

        return response;
    }

    /**
     * 获取缓存的登录信息
     * @param httpSession
     * @return
     */
    private JSONObject getLoginSession(HttpSession httpSession) {
        JSONObject loginSessionObject = (JSONObject) httpSession.getAttribute("ossLoginUser");
        return loginSessionObject;
    }
}

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
import java.util.List;
import java.util.Map;

/**
 * @Description: 测试模块控制器
 * @author: huang.jing
 * @Date: 2018/1/19 0019 - 10:16
 */
@RestController
@RequestMapping("/controller/service/test")
public class TestServiceController {

    private static Logger log = LogManager.getLogger(TestServiceController.class);

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

        // 测试模块可以查询到所有待测试的定单，不限制创建人
        JSONObject qryJson = new JSONObject();
        qryJson.put("orderTitle", orderTitle);
        qryJson.put("zmpId", zmpId);
        qryJson.put("orderState", orderState);
        qryJson.put("version", version);
        qryJson.put("tacheId", tacheId);
        qryJson.put("orderType", "test");
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
     * 测试任务单测试模块回单
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
        finishOrderJson.put("orderType", "test");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");

        if(developOrderManager.finishWorkOrder(finishOrderJson)) {
            response.success(returnData);
        } else {
            response.failure("回单失败");
        }

        return response;
    }

    /**
     * 测试任务单测试模块退单
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
        finishOrderJson.put("orderType", "test");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");


        boolean returnFlag = false;
        try {
            // 退单时，工单的接收人设置为定单创建人
            JSONObject qryJson = new JSONObject();
            qryJson.put("page", 1);
            qryJson.put("limit", 1);
            qryJson.put("developOrderId", finishOrderJson.getString("developOrderId"));
            JSONObject developOrderListJson = developOrderManager.getDevelopOrderList(qryJson);
            List orderList = (List) developOrderListJson.get("orderList");
            if (orderList.size() > 0) {
                Map orderMap = (Map) orderList.get(0);
                finishOrderJson.put("nextWorkOrderOper", orderMap.get("AUTHOR"));
                returnFlag = true;
            }
        } catch (Exception e) {
            log.error("根据任务单号查询任务信息异常", e);
        }

        if(returnFlag && developOrderManager.returnWorkOrder(finishOrderJson)) {
            response.success(returnData);
        } else {
            response.failure("回单失败");
        }

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

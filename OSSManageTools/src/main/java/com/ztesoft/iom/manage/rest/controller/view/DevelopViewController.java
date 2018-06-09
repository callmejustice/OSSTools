package com.ztesoft.iom.manage.rest.controller.view;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.develop.service.DevelopOrderManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;

/**
 * @Description: 研发模块视图控制器，从/WEB-INF/views/develop目录下寻找指定的页面并构造成视图返回
 * @author: huang.jing
 * @Date: 2018/1/13 0013 - 16:05
 */
@RestController
@RequestMapping("/controller/views/develop")
public class DevelopViewController {
    /**
     * 研发任务单管理
     *
     * @return
     */
    @RequestMapping("/orderManage.do")
    public ModelAndView orderManage() {
        ModelAndView mav = new ModelAndView("develop/orderManage");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        JSONObject qryJson = new JSONObject();
        JSONObject versionJson = developOrderManager.getVersionList(qryJson);
        mav.addObject("versionJson", versionJson.toString());
        return mav;
    }

    /**
     * 提交研发任务单
     *
     * @return
     */
    @RequestMapping("/createOrder.do")
    public ModelAndView createOrder() {
        ModelAndView mav = new ModelAndView("develop/createOrder");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        JSONObject qryJson = new JSONObject();
        JSONObject versionJson = developOrderManager.getVersionList(qryJson);
        mav.addObject("versionJson", versionJson.toString());
        return mav;
    }

    /**
     * 关联SVN代码
     *
     * @return
     */
    @RequestMapping("/attachSVN.do")
    public ModelAndView svnList(@RequestParam(value = "developOrderId", required = false, defaultValue = "") String developOrderId
            , @RequestParam(value = "workOrderId", required = false, defaultValue = "") String workOrderId
            , @RequestParam(value = "orderTitle", required = false, defaultValue = "") String orderTitle) {
        ModelAndView mav = new ModelAndView("develop/attachSVN");
        mav.addObject("developOrderId", developOrderId);
        mav.addObject("workOrderId", workOrderId);
        try {
            orderTitle = URLDecoder.decode(orderTitle, "utf-8");
        } catch (Exception e) { }
        mav.addObject("orderTitle", orderTitle);
        return mav;
    }
}

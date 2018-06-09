package com.ztesoft.iom.manage.rest.controller.view;

import com.alibaba.fastjson.JSONObject;
import com.ztesoft.iom.common.util.BeanFactory;
import com.ztesoft.iom.manage.develop.service.DevelopOrderManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 测试模块视图控制器，从/WEB-INF/views/test目录下寻找指定的页面并构造成视图返回
 * @author: huang.jing
 * @Date: 2018/1/18 0018 - 20:53
 */
@RestController
@RequestMapping("/controller/views/test")
public class TestViewController {
    /**
     * 待测试任务单管理
     *
     * @return
     */
    @RequestMapping("/orderManage.do")
    public ModelAndView orderManage() {
        ModelAndView mav = new ModelAndView("test/orderManage");
        DevelopOrderManager developOrderManager = (DevelopOrderManager) BeanFactory.getApplicationContext().getBean("developOrderManager");
        JSONObject qryJson = new JSONObject();
        JSONObject versionJson = developOrderManager.getVersionList(qryJson);
        mav.addObject("versionJson", versionJson.toString());
        return mav;
    }
}

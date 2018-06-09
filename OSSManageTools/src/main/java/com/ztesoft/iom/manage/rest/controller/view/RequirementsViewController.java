package com.ztesoft.iom.manage.rest.controller.view;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 需求模块视图控制器，从/WEB-INF/views/requirements目录下寻找指定的页面并构造成视图返回
 * @author: huang.jing
 * @Date: 2018/1/13 0013 - 16:07
 */
@RestController
@RequestMapping("/controller/views/requirements")
public class RequirementsViewController {
    /**
     * 需求任务单管理
     *
     * @return
     */
    @RequestMapping("/orderManage.do")
    public ModelAndView requirementsList() {
        return new ModelAndView("requirements/orderManage");
    }
}

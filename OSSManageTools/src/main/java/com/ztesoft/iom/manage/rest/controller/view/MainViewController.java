package com.ztesoft.iom.manage.rest.controller.view;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 主视图控制器，从/WEB-INF/views/目录下寻找指定的页面并构造成视图返回
 * @author: huang.jing
 * @Date: 2017/12/31 0031 - 19:23
 */
@RestController
@RequestMapping("/controller/views")
public class MainViewController {
    /**
     * 登录后的主页面
     *
     * @return
     */
    @RequestMapping("/main.do")
    public ModelAndView main() {
        return new ModelAndView("main");
    }
}

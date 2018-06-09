package com.ztesoft.iom.manage.rest.controller.view;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description: 知识库模块视图控制器
 * @author: huang.jing
 * @Date: 2018/3/14 0014 - 18:02
 */
@RestController
@RequestMapping("/controller/views/kbs")
public class KBSViewController {

    /**
     * 知识库管理
     *
     * @return
     */
    @RequestMapping("/knowledgeManage.do")
    public ModelAndView knowledgeManage() {
        return new ModelAndView("kbs/knowledgeManage");
    }

    /**
     * 新建知识点
     *
     * @return
     */
    @RequestMapping("/createKnowledge.do")
    public ModelAndView createKnowledge() {
        return new ModelAndView("kbs/createKnowledge");
    }

    /**
     * 修改知识点
     *
     * @return
     */
    @RequestMapping("/editKnowledge.do")
    public ModelAndView editKnowledge(@RequestParam(value = "knowledgeId", required = false, defaultValue = "") String knowledgeId) {
        ModelAndView mav = new ModelAndView("kbs/editKnowledge");
        mav.addObject("knowledgeId", knowledgeId);
        return mav;
    }
}

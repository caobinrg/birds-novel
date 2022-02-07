package com.leqiwl.novel.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/28 17:47
 * @Description:
 */
@Controller
@RequestMapping("/console")
public class ConsoleController {

    @GetMapping("")
    public String console(){
        return "view/console/console";
    }



    @GetMapping("dataStatistics")
    public String dataStatistics(){
        return "view/console/dataStatistics";
    }

    @GetMapping("dataAnalysis")
    public String dataAnalysis(){
        return "view/console/dataAnalysis";
    }



}

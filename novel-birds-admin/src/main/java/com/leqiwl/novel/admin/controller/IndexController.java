package com.leqiwl.novel.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/28 17:04
 * @Description:
 */
@Controller
public class IndexController {

//    @RequestMapping({"*"})
//    public void pathError(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/login");
//    }

    @RequestMapping("/view/{project}/{view}")
    public String devPath(@PathVariable String project,@PathVariable String view){
        return "view/" + project + "/" + view;
    }


    @RequestMapping({"/index"})
    public String index(){
        return "index";
    }
}

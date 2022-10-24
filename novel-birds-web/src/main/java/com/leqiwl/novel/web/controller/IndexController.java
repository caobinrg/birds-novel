package com.leqiwl.novel.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/5 21:22
 * @Description:
 */
@Controller
public class IndexController {

    @Value("${mapping.home:/web/home}")
    private String home;

    @RequestMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(home);
        requestDispatcher.forward(request,response);
    }

//    @RequestMapping({"*"})
//    public void redirect(HttpServletResponse response) throws IOException {
//        response.sendRedirect(home);
//    }

}

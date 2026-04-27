package com.courier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PageController — serves all Thymeleaf HTML pages
 */
@Controller
public class PageController {

    @GetMapping("/")           public String index()    { return "index"; }
    @GetMapping("/admin")      public String admin()    { return "admin"; }
    @GetMapping("/employee")   public String employee() { return "employee"; }
    @GetMapping("/customer")   public String customer() { return "customer"; }
}

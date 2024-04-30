package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}

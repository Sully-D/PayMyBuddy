package com.payMyBuddy.payMyBuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginController {

    @GetMapping("/login")
    public ModelAndView loginPage() {
        return new ModelAndView("login");
    }

//    @PostMapping("/login")
//    public String handleLogin(String email, String password, boolean rememberMe) {
//        return "redirect:/home";
//    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}

package com.payMyBuddy.payMyBuddy.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
public class LogoutController {

    @GetMapping("/logout")
    public ModelAndView logoutPage(Model model) {
        return new ModelAndView("logout");
    }
}
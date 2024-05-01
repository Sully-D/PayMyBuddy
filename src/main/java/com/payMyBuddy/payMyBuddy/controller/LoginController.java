package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("userAccount", new UserAccount());
        return "index";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("userForm") UserAccount userAccount, BindingResult result) {
        if (result.hasErrors()) {
            return "index";
        }

        userService.createUser(userAccount);

        return "redirect:/login";
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

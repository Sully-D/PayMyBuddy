package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
public class LoginController {

    @Autowired
    UserService userService;

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

    @GetMapping("/home")
    public ModelAndView homePage(Model model) {
        Optional<UserAccount> userOptional = userService.getCurrentUser();
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
        } else {
            return new ModelAndView("login");
        }
        return new ModelAndView("index");
    }

}

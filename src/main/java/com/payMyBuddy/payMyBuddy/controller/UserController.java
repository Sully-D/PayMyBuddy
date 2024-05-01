package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SenderRecipientConnectionService senderRecipientConnectionService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

//    @GetMapping("/home")
//    public String homePage(Model model) {
//        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
//        if (optionalUserAccount.isPresent()) {
//            model.addAttribute("user", optionalUserAccount.get());
//        } else {
//            return "login";
//        }
//        return "home";
//    }
    @GetMapping("/home")
    public String homePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(email);
        logger.debug("Loading home for user: {}", email);
        if (optionalUserAccount.isPresent()) {
            model.addAttribute("user", optionalUserAccount.get());
        } else {
            logger.error("No user found with email: {}", email);
            return "login";
        }
        return "home";
    }

    @GetMapping("/transfer")
    public String transferPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(email);
        if (optionalUserAccount.isPresent()) {
            UserAccount userAccount = optionalUserAccount.get();
            List<String> connections = senderRecipientConnectionService.getConnection(userAccount);
            model.addAttribute("user", userAccount);
            model.addAttribute("connections", connections);
        } else {
            return "login";
        }
        return "transfer";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (optionalUserAccount.isPresent()) {
            model.addAttribute("user", optionalUserAccount.get());
        } else {
            return "login";
        }
        return "profile";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (optionalUserAccount.isPresent()) {
            model.addAttribute("user", optionalUserAccount.get());
        } else {
            return "login";
        }
        return "contact";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam("id") long id,
                                @RequestParam("firstname") String firstname,
                                @RequestParam("lastname") String lastname) {
        try {
            userService.editProfile(id, firstname, lastname);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
    }

    @PostMapping("/addFunds")
    public String addFunds(@RequestParam("id") long id,
                           @RequestParam("amount") BigDecimal amount) {
        try {
            userService.addFund(id, amount);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
    }

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam("id") long id,
                            @RequestParam("email") String email) {

        UserAccount currentUser = null;
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (optionalUserAccount.isPresent()) {
            currentUser = optionalUserAccount.get();
        } else {
            return "login";
        }

        UserAccount friend = null;
        Optional<UserAccount> optionalFriend = userRepository.findByEmail(email);
        if (optionalFriend.isPresent()) {
            friend = optionalFriend.get();
        } else {
            return "error";
        }

        try {
            senderRecipientConnectionService.createConnection(currentUser, friend);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
    }
}

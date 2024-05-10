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

    /**
     * Redirects the root URL to the registration page.
     *
     * @return A redirect string to the registration URL.
     */
    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    /**
     * Displays the login page.
     *
     * @return The name of the login view template.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Handles the user logout process and redirects to the login page with a logout indicator.
     *
     * @return A redirect string to the login URL with a logout query parameter.
     */
    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    /**
     * Displays the user registration form.
     * Initializes a new UserAccount object and adds it to the model.
     *
     * @param model The model to hold the new user account data.
     * @return The name of the registration form view template.
     */
    @GetMapping("/register")
    public String registrationForm(Model model) {
        // Add a new, empty UserAccount object to the model for binding to the form
        model.addAttribute("userAccount", new UserAccount());
        return "index";
    }

    /**
     * Handles the user registration form submission.
     * Validates the input data and creates a new user account if valid.
     *
     * @param userAccount The user account data submitted via the form.
     * @param result The BindingResult for validation error detection.
     * @return A redirect string to the appropriate URL depending on success or failure.
     */
    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("userForm") UserAccount userAccount, BindingResult result) {
        // Redirect to the registration form if validation errors occur
        if (result.hasErrors()) {
            return "redirect:/index";
        }

        // Create the new user account using the user service
        userService.createUser(userAccount);

        // Redirect to the login page upon successful registration
        return "redirect:/login";
    }

    /**
     * Displays the success page.
     *
     * @return The name of the success view template.
     */
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    /**
     * Displays the error page.
     *
     * @return The name of the error view template.
     */
    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }
}

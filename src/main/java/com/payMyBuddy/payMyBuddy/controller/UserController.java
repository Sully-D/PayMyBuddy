package com.payMyBuddy.payMyBuddy.controller;

import com.payMyBuddy.payMyBuddy.model.Transaction;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Autowired
    SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    @Autowired
    TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    public String transferPage(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "4") int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(email);
        if (!optionalUserAccount.isPresent()) {
            return "redirect:/login";
        }

        UserAccount userAccount = optionalUserAccount.get();
        List<String> connections = senderRecipientConnectionService.getConnection(userAccount);
        Page<Transaction> transactionsPage = transactionService.getTransaction(userAccount, page, size);
        model.addAttribute("user", userAccount);
        model.addAttribute("connections", connections);
        model.addAttribute("transactionsPage", transactionsPage);

        return "transfer";
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (optionalUserAccount.isPresent()) {
            model.addAttribute("user", optionalUserAccount.get());
        } else {
            return "redirect:/login";
        }
        return "profile";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (optionalUserAccount.isPresent()) {
            model.addAttribute("user", optionalUserAccount.get());
        } else {
            return "redirect:/login";
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
            return "redirect:/login";
        }

        UserAccount friend = null;
        Optional<UserAccount> optionalFriend = userRepository.findByEmail(email);
        if (optionalFriend.isPresent()) {
            friend = optionalFriend.get();
        } else {
            return "redirect:/error";
        }

        try {
            senderRecipientConnectionService.createConnection(currentUser, friend);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
    }

    @PostMapping("/pay")
    public String processPayment(@RequestParam("recipient") int recipientIndex,
                                 @RequestParam("amount") BigDecimal amount,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalSender = userRepository.findByEmail(email);

        if (!optionalSender.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Invalid session or user not found.");
            return "redirect:/login";
        }

        UserAccount sender = optionalSender.get();
        List<UserAccount> recipients = senderRecipientConnectionRepository.findRecipientsBySenderId(sender.getId());

        if (recipientIndex < 0 || recipientIndex >= recipients.size()) {
            redirectAttributes.addFlashAttribute("error", "Invalid recipient.");
            return "redirect:/transfer";
        }

        UserAccount recipient = recipients.get(recipientIndex);

        try {
            Transaction transaction = Transaction.builder()
                    .sender(sender)
                    .recipient(recipient)
                    .amount(amount)
                    .date( LocalDateTime.now())
                    .description("Transfer from " + sender.getEmail() + " to " + recipient.getEmail())
                    .build();

            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("success", "Transaction completed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Transaction failed: " + e.getMessage());
            return "redirect:/transfer";
        }

        return "redirect:/transfer";
    }

    @PostMapping("/withdraw")
    public String handleWithdraw(@RequestParam String iban, @RequestParam BigDecimal amount) {
        try {
            userService.withdraw(amount, iban);
            return "redirect:/home?withdraw=success";
        } catch (Exception e) {
            return "redirect:/home?withdraw=error";
        }
    }

}

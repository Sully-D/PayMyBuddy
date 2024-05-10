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

    /**
     * Displays the home page for the authenticated user, or redirects to login if not authenticated.
     *
     * @param model The model to hold user attributes.
     * @return The name of the home view template.
     */
    @GetMapping("/home")
    public String homePage(Model model) {
        // Get the current authenticated user's email
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

    /**
     * Displays the transfer page for the authenticated user, including their connections and transactions.
     *
     * @param model The model to hold page attributes.
     * @param page The page number for transaction pagination.
     * @param size The number of transactions per page.
     * @return The name of the transfer view template.
     */
    @GetMapping("/transfer")
    public String transferPage(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "4") int size) {
        // Retrieve the current authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalUserAccount = userRepository.findByEmail(email);

        // Redirect to login if no authenticated user is found
        if (!optionalUserAccount.isPresent()) {
            return "redirect:/login";
        }

        // Fetch user details, connections, and transactions
        UserAccount userAccount = optionalUserAccount.get();
        List<String> connections = senderRecipientConnectionService.getConnection(userAccount);
        Page<Transaction> transactionsPage = transactionService.getTransaction(userAccount, page, size);

        // Populate the model with relevant data
        model.addAttribute("user", userAccount);
        model.addAttribute("connections", connections);
        model.addAttribute("transactionsPage", transactionsPage);

        return "transfer";
    }

    /**
     * Displays the profile page for the authenticated user, or redirects to login if not authenticated.
     *
     * @param model The model to hold user attributes.
     * @return The name of the profile view template.
     */
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

    /**
     * Displays the contact page for the authenticated user, or redirects to login if not authenticated.
     *
     * @param model The model to hold user attributes.
     * @return The name of the contact view template.
     */
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

    /**
     * Updates the user's profile information with the provided first and last names.
     *
     * @param id The unique user ID.
     * @param firstname The updated first name.
     * @param lastname The updated last name.
     * @return A redirect URL back to the profile page with a success or error query parameter.
     */
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

    /**
     * Adds funds to the user's account with the provided amount.
     *
     * @param id The unique user ID.
     * @param amount The amount to be added to the account.
     * @return A redirect URL back to the profile page with a success or error query parameter.
     */
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

    /**
     * Adds a new friend connection for the authenticated user.
     *
     * @param id The unique user ID.
     * @param email The email of the friend to be added.
     * @return A redirect URL back to the profile page with a success or error query parameter.
     */
    @PostMapping("/addFriend")
    public String addFriend(@RequestParam("id") long id,
                            @RequestParam("email") String email) {
        // Retrieve the currently authenticated user
        Optional<UserAccount> optionalUserAccount = userService.getCurrentUser();
        if (!optionalUserAccount.isPresent()) {
            return "redirect:/login";
        }
        UserAccount currentUser = optionalUserAccount.get();

        // Retrieve the friend's account by email
        Optional<UserAccount> optionalFriend = userRepository.findByEmail(email);
        if (!optionalFriend.isPresent()) {
            return "redirect:/error";
        }
        UserAccount friend = optionalFriend.get();

        // Create the friend connection
        try {
            senderRecipientConnectionService.createConnection(currentUser, friend);
            return "redirect:/profile?success";
        } catch (IllegalArgumentException e) {
            return "redirect:/profile?error";
        }
    }

    /**
     * Processes a payment to the specified recipient from the authenticated user's account.
     *
     * @param recipientIndex The index of the recipient in the user's connection list.
     * @param amount The amount to be paid.
     * @param redirectAttributes Attributes to store flash messages.
     * @return A redirect URL back to the transfer page with a success or error message.
     */
    @PostMapping("/pay")
    public String processPayment(@RequestParam("recipient") int recipientIndex,
                                 @RequestParam("amount") BigDecimal amount,
                                 RedirectAttributes redirectAttributes) {
        // Retrieve the current authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<UserAccount> optionalSender = userRepository.findByEmail(email);

        // Redirect to login if the sender is not authenticated
        if (!optionalSender.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Invalid session or user not found.");
            return "redirect:/login";
        }

        UserAccount sender = optionalSender.get();
        List<UserAccount> recipients = senderRecipientConnectionRepository.findRecipientsBySenderId(sender.getId());

        // Ensure the recipient index is within bounds
        if (recipientIndex < 0 || recipientIndex >= recipients.size()) {
            redirectAttributes.addFlashAttribute("error", "Invalid recipient.");
            return "redirect:/transfer";
        }

        UserAccount recipient = recipients.get(recipientIndex);

        // Create the transaction and process payment
        try {
            Transaction transaction = Transaction.builder()
                    .sender(sender)
                    .recipient(recipient)
                    .amount(amount)
                    .date(LocalDateTime.now())
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

    /**
     * Handles a withdrawal request for the authenticated user.
     *
     * @param iban The IBAN of the account where the funds will be withdrawn.
     * @param amount The amount to withdraw from the user's balance.
     * @return A redirect URL back to the home page with a success or error query parameter.
     */
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

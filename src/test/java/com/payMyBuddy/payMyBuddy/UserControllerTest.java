package com.payMyBuddy.payMyBuddy;


import com.payMyBuddy.payMyBuddy.controller.UserController;
import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.SenderRecipientConnectionRepository;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import com.payMyBuddy.payMyBuddy.service.SenderRecipientConnectionService;
import com.payMyBuddy.payMyBuddy.service.TransactionService;
import com.payMyBuddy.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SenderRecipientConnectionService senderRecipientConnectionService;

    @MockBean
    private SenderRecipientConnectionRepository senderRecipientConnectionRepository;

    @MockBean
    private TransactionService transactionService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testHomePage_UserExists_ShouldReturnHome() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new UserAccount()));

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void testTransferPage_UserNotFound_ShouldRedirectToLogin() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void testAddFunds_Success_ShouldRedirectWithSuccess() throws Exception {
        mockMvc.perform(post("/addFunds")
                        .param("id", "1")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    @WithMockUser
    public void profilePage_WhenUserExists_ShouldReturnProfileView() throws Exception {
        UserAccount user = new UserAccount();
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    public void profilePage_WhenUserDoesNotExist_ShouldRedirectToLogin() throws Exception {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void contactPage_WhenUserExists_ShouldReturnContactView() throws Exception {
        UserAccount user = new UserAccount();
        when(userService.getCurrentUser()).thenReturn(Optional.of(user));

        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    public void contactPage_WhenUserDoesNotExist_ShouldRedirectToLogin() throws Exception {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());

        mockMvc.perform(get("/contact"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void updateProfile_Success_ShouldRedirectWithSuccess() throws Exception {
        doNothing().when(userService).editProfile(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/updateProfile")
                        .param("id", "1")
                        .param("firstname", "John")
                        .param("lastname", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    @WithMockUser
    public void updateProfile_Failure_ShouldRedirectWithError() throws Exception {
        doThrow(new IllegalArgumentException()).when(userService).editProfile(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/updateProfile")
                        .param("id", "1")
                        .param("firstname", "John")
                        .param("lastname", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error"));
    }

    @Test
    @WithMockUser
    public void addFriend_UserNotLoggedIn_ShouldRedirectToLogin() throws Exception {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());

        mockMvc.perform(post("/addFriend")
                        .param("id", "1")
                        .param("email", "friend@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void addFriend_FriendNotFound_ShouldRedirectToError() throws Exception {
        when(userService.getCurrentUser()).thenReturn(Optional.of(new UserAccount()));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/addFriend")
                        .param("id", "1")
                        .param("email", "nonexistent@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    @WithMockUser
    public void addFriend_Success_ShouldRedirectToProfileWithSuccess() throws Exception {
        UserAccount user = new UserAccount();
        UserAccount friend = new UserAccount();

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(friend));
        doNothing().when(senderRecipientConnectionService).createConnection(user, friend);

        mockMvc.perform(post("/addFriend")
                        .param("id", "1")
                        .param("email", "friend@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    @WithMockUser
    public void addFriend_Exception_ShouldRedirectToProfileWithError() throws Exception {
        UserAccount user = new UserAccount();
        UserAccount friend = new UserAccount();

        when(userService.getCurrentUser()).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(friend));
        doThrow(new IllegalArgumentException()).when(senderRecipientConnectionService).createConnection(user, friend);

        mockMvc.perform(post("/addFriend")
                        .param("id", "1")
                        .param("email", "friend@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void processPayment_SenderNotFound_ShouldRedirectToLogin() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/pay")
                        .param("recipient", "1")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("error", "Invalid session or user not found."));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void processPayment_InvalidRecipientIndex_ShouldRedirectToTransferWithError() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new UserAccount()));
        when(senderRecipientConnectionRepository.findRecipientsBySenderId(any())).thenReturn(Arrays.asList(new UserAccount()));

        mockMvc.perform(post("/pay")
                        .param("recipient", "10")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attribute("error", "Invalid recipient."));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void processPayment_SuccessfulTransaction_ShouldRedirectWithSuccess() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new UserAccount()));
        when(senderRecipientConnectionRepository.findRecipientsBySenderId(any())).thenReturn(Arrays.asList(new UserAccount()));

        mockMvc.perform(post("/pay")
                        .param("recipient", "0")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attribute("success", "Transaction completed successfully."));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void processPayment_TransactionFails_ShouldRedirectWithError() throws Exception {
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(new UserAccount()));
        when(senderRecipientConnectionRepository.findRecipientsBySenderId(any())).thenReturn(Arrays.asList(new UserAccount()));
        doThrow(new RuntimeException("Transaction failed")).when(transactionService).createTransaction(any());

        mockMvc.perform(post("/pay")
                        .param("recipient", "0")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfer"))
                .andExpect(flash().attribute("error", "Transaction failed: Transaction failed"));
    }


}

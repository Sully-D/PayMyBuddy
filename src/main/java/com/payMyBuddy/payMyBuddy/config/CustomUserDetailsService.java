package com.payMyBuddy.payMyBuddy.config;

import com.payMyBuddy.payMyBuddy.model.UserAccount;
import com.payMyBuddy.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of the UserDetailsService interface for authentication with Spring Security.
 * This service is used by Spring Security during the authentication process to load user-specific data.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user's data from the database using the email as the username.
     *
     * @param email The email address used as the username to retrieve user details.
     * @return UserDetails containing the user's email, password, and authorities.
     * @throws UsernameNotFoundException if no user is found with the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetching user from the repository
        Optional<UserAccount> optionalUser = userRepository.findByEmail(email);
        UserAccount user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("No user found : " + email));

        // Returning user details required by Spring Security for authentication and authorization
        return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user.getRole()));
    }

    /**
     * Helper method to create a list of GrantedAuthority objects from a user role.
     * Prefixes each role with "ROLE_" which is a convention used by Spring Security.
     *
     * @param role The security role of the user.
     * @return A list of GrantedAuthority objects representing the user's security roles.
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}

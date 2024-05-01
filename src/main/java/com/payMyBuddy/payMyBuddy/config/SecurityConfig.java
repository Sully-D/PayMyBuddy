package com.payMyBuddy.payMyBuddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Security configuration class for Spring Security.
 * Sets up authentication mechanisms, authorization rules, and password encoding.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Bean configuration for the BCrypt password encoder.
     * @return BCryptPasswordEncoder A password encoder instance to hash and verify passwords.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures security filter chain for HTTP requests.
     *
     * @param http HttpSecurity context used for configuring web-based security.
     * @return SecurityFilterChain The security filter chain configured for handling HTTP requests.
     * @throws Exception if there is a problem during configuration.
     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        return http.authorizeHttpRequests(auth -> {
//            auth.requestMatchers("/admin").hasRole("ADMIN");
//            auth.requestMatchers("/user").hasRole("USER");
//            auth.anyRequest().authenticated();
//        }).formLogin(Customizer.withDefaults()).build();
//    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/user").hasRole("USER")
                        //.requestMatchers("/profile").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400) // 1 day
                )
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    /**
     * Creates a bean for the Authentication Manager which integrates custom user details and password encoder.
     *
     * @param http HttpSecurity context for obtaining shared objects.
     * @param bCryptPasswordEncoder BCryptPasswordEncoder to encode passwords.
     * @return AuthenticationManager An authentication manager configured with custom user details service and password encoder.
     * @throws Exception if there is a problem during configuration.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            SecurityContextHolder.clearContext();
            response.sendRedirect("/login"); // Redirection après déconnexion
        };
    }
}

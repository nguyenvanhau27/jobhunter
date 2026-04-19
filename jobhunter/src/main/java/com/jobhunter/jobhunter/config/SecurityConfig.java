package com.jobhunter.jobhunter.config;

import com.jobhunter.jobhunter.repository.UserRepository;
import com.jobhunter.jobhunter.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public CustomAuthFailureHandler customAuthFailureHandler() {
        return new CustomAuthFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http
                .userDetailsService(userDetailsService)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/static/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/register", "/login", "/forgot-password", "/reset-password").permitAll()
                        .requestMatchers("/", "/home", "/jobs", "/jobs/**").permitAll()
                        .requestMatchers("/companies", "/companies/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/profile/**").hasRole("USER")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/applications/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(customAuthFailureHandler())
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        // lastLogin update in CustomUserDetailsService
                        .successHandler((request, response, authentication) -> {
                            String role = authentication.getAuthorities()
                                    .iterator().next().getAuthority();
                            if ("ROLE_ADMIN".equals(role)) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
//                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}




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
                        .requestMatchers("/jobs", "/jobs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/profile/**").hasRole("USER")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/applications/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            String email = authentication.getName();
                            userRepository.findByEmail(email).ifPresent(user -> {
                                user.setLastLogin(LocalDateTime.now()); //save last_login
                                userRepository.save(user);
                            });
                            String role = authentication.getAuthorities()
                                    .iterator().next().getAuthority();
                            System.out.println("=================================");
                            System.out.println("LOGIN SUCCESS");
                            System.out.println("Email  : " + email);
                            System.out.println("Role   : " + role);
                            System.out.println("Time   : " + java.time.LocalDateTime.now());
                            System.out.println("=================================");
                            if ("ROLE_ADMIN".equals(role)) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            String email = request.getParameter("email");
                            System.out.println("=================================");
                            System.out.println("LOGIN FAILED");
                            System.out.println("Email  : " + email);
                            System.out.println("Reason : " + exception.getMessage());
                            System.out.println("Time   : " + java.time.LocalDateTime.now());
                            System.out.println("=================================");

                            response.sendRedirect("/login?error=true");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // ✅ Log khi logout
                            String email = authentication != null ? authentication.getName() : "unknown";
                            System.out.println("=================================");
                            System.out.println("LOGOUT SUCCESS");
                            System.out.println("Email  : " + email);
                            System.out.println("Time   : " + java.time.LocalDateTime.now());
                            System.out.println("=================================");

                            response.sendRedirect("/login?logout=true");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}




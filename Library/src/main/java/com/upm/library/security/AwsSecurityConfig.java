package com.upm.library.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
@Profile("aws")
public class AwsSecurityConfig {

    @Value("spring.security.oauth2.client.registration.cognito.client-id")
    private String clientId;

    @Value("library.cognito.domain")
    private String cognitoDomain;

    @Value("library.cognito.logout-redirect")
    private String logoutRedirect;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/public/**", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("LIBRARIAN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> {}).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(cognitoLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    LogoutSuccessHandler cognitoLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            String encoded = URLEncoder.encode(logoutRedirect, StandardCharsets.UTF_8);
            String url = cognitoDomain
                    + "/logout?client_id=" + clientId
                    + "&logout_uri=" + encoded;

            response.sendRedirect(url);
        };
    }
}



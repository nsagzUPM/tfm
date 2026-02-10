package com.upm.library.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Stream;

@Configuration
@EnableMethodSecurity
@Profile("aws")
public class AwsSecurityConfig {

    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;

    @Value("${library.cognito.domain}")
    private String cognitoDomain;

    @Value("${library.cognito.logout-redirect}")
    private String logoutRedirect;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/menu", "/catalog/**", "/my-account/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("LIBRARIAN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(cognitoAuthoritiesMapper())
                        )
                        .defaultSuccessUrl("/menu", true)).logout(logout -> logout
                        .logoutUrl("/logout").logoutSuccessUrl("/")
                        .logoutSuccessHandler(cognitoLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper cognitoAuthoritiesMapper() {
        return (authorities) -> authorities.stream()
                .flatMap(a -> {
                    // Mantén las authorities existentes (OIDC_USER, etc.)
                    Stream<GrantedAuthority> base = Stream.of(a);

                    if (a instanceof OidcUserAuthority oidc) {
                        Object groups = oidc.getIdToken().getClaims().get("cognito:groups");

                        if (groups instanceof Collection<?> list) {
                            Stream<GrantedAuthority> roles = list.stream()
                                    .map(Object::toString)
                                    .map(g -> new SimpleGrantedAuthority("ROLE_" + g));

                            return Stream.concat(base, roles);
                        }
                    }

                    return base;
                })
                // evita duplicados
                .collect(java.util.stream.Collectors.toSet());
    }


    @Bean
    LogoutSuccessHandler cognitoLogoutSuccessHandler()  {
        return (request, response, authentication) -> {
            String encoded = URLEncoder.encode(logoutRedirect, StandardCharsets.UTF_8);
            String url = cognitoDomain
                    + "/logout?client_id=" + clientId
                    + "&logout_uri=" + encoded;

            response.sendRedirect(url);
        };
    }
}



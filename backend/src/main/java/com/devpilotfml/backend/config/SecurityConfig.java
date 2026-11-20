package com.devpilotfml.backend.config;

import com.devpilotfml.backend.security.GithubOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GithubOAuth2UserService githubOAuth2UserService;
    private final AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final AuthenticationFailureHandler oAuth2FailureHandler;

    @Value("${server.servlet.session.cookie.name}")
    private String cookieName;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            // Permitted without authentication
            .authorizeHttpRequests(auth -> auth.requestMatchers(
                    "/api/auth/login-url",
                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/error")
                .permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Authenticated
                .requestMatchers("/api/**").authenticated()
                // Rest all allowed
                .anyRequest().permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(githubOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                // Clear on logout
                .logout(logout -> logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler((req, res, auth) ->
                        res.setStatus(HttpStatus.NO_CONTENT.value())
                    )
                    .invalidateHttpSession(true)
                        .clearAuthentication(true)
                    .deleteCookies(cookieName)
                );

        return httpSecurity.build();
    }
}

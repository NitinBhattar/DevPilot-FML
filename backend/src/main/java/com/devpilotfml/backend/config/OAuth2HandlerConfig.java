package com.devpilotfml.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
public class OAuth2HandlerConfig {
    @Bean
    AuthenticationSuccessHandler oAuth2SuccessHandler(@Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl(frontendUrl + "/auth/callback");

        return successHandler;
    }

    @Bean
    AuthenticationFailureHandler oAuth2FailureHandler(@Value("${app.frontend-url}") String frontendUrl) {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setDefaultFailureUrl(frontendUrl + "/login?error=oauth_failed");

        return failureHandler;
    }
}

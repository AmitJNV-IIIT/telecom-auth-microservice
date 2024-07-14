package com.excitel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * This class defines Spring Security configuration beans for the application.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@Configuration
@EnableWebSecurity
public class AppConfig{
    /**
     * Creates and returns a bean of type {@link BCryptPasswordEncoder}.
     * This bean is used for password encoding during authentication.
     *
     * @return A bean of type {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Creates and returns a bean of type {@link AuthenticationManager}.
     * This bean is used to manage user authentication in the application.
     *
     * @param builder An instance of {@link AuthenticationConfiguration}
     * used to retrieve the built {@link AuthenticationManager}.
     * @return A bean of type {@link AuthenticationManager}.
     * @throws Exception If there is an error while retrieving the
     * {@link AuthenticationManager} from the {@link AuthenticationConfiguration}.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}

package com.excitel.config;

import com.excitel.dynamodbsdkhelper.AuthQueryBuilder;
import com.excitel.security.JwtAuthenticationEntryPoint;
import com.excitel.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Arrays;
@EnableWebSecurity
@Configuration
public class SecurityConfig{
    @Autowired //NOSONAR
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired //NOSONAR
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired //NOSONAR
    private DynamoDbClient dynamoDbClient;

    @Autowired //NOSONAR
    private UserDetailsService dynamoDbUserDetailsService;

    @Autowired //NOSONAR
    private AuthQueryBuilder authQueryBuilder;

    @Autowired //NOSONAR
    private PasswordEncoder passwordEncoder;
    /**
     * Configures security settings and filters.
     *
     * @param http HttpSecurity object to configure security settings
     * @return SecurityFilterChain object representing the security filter chain
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf-> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeRequests()
                .requestMatchers("/api/v2/auth/users").hasAuthority("ADMIN")
                .requestMatchers("/api/v2/auth/register","/api/v2/auth/health",
                        "/api/v2/auth/login","/api/v2/auth/password/**","/actuator/**","/actuator").permitAll()
                .requestMatchers("/api/v2/auth/user").authenticated()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling(ex->ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     *
     * @return CorsConfigurationSource object representing the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}

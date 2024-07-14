package com.excitel.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppConfigTest {
    @InjectMocks
    private AppConfig appConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Test
    void whenPasswordEncoder_thenBCryptPasswordEncoderIsReturned() {
        PasswordEncoder passwordEncoder = appConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void whenAuthenticationManager_thenAuthenticationManagerIsReturned() throws Exception {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager returnedAuthenticationManager = appConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(returnedAuthenticationManager);
        assertEquals(authenticationManager, returnedAuthenticationManager);
    }

}

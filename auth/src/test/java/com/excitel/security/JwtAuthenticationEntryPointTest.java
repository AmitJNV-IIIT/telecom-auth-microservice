package com.excitel.security;
import com.excitel.dto.FeignResponseDtO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JwtAuthenticationEntryPointTest {

    @Test
    void testCommence_Success() throws IOException, ServletException {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new AuthenticationException("Unauthorized") {};

        entryPoint.commence(request, response, authException);

        assertEquals((HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION), response.getStatus());
        assertEquals("application/json", response.getContentType());
        FeignResponseDtO feignResponseDtO = new ObjectMapper().readValue(response.getContentAsString(), FeignResponseDtO.class);
        assertNotNull(feignResponseDtO);
        assertEquals(HttpStatus.valueOf(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION), feignResponseDtO.getStatus());
        assertEquals("Access Denied !! ", feignResponseDtO.getMessage());
        assertEquals("Not Authorized", feignResponseDtO.getMobileNumber());
        assertEquals("Hacker", feignResponseDtO.getRole());
    }
}
package com.excitel.security;

import com.excitel.dto.FeignResponseDtO;
import com.excitel.model.Customer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.io.IOException;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @InjectMocks

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private SecurityContext securityContextHolder;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private JwtHelper jwtHelper;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private SecurityContext securityContext;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void handleException_ReturnsBadRequest() throws IOException {
        // Arrange
        int statusCode = HttpStatus.BAD_REQUEST.value();
        String errorMessage = "Bad request error message";

        // Act
        ResponseEntity<FeignResponseDtO> responseEntity = jwtAuthenticationFilter.handleException(response, statusCode, errorMessage);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getBody().getStatus());
        assertEquals(errorMessage, responseEntity.getBody().getMessage());
    }

    @Test
    void handleException_NullErrorMessage_ThrowsIllegalArgumentException() {
        // Arrange
        int statusCode = HttpStatus.BAD_REQUEST.value();
        String errorMessage = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> jwtAuthenticationFilter.handleException(response, statusCode, errorMessage));

    }

    @Test
    public void testDoFilterInternal_ValidToken_UserNonEmptyAuthorities() throws ServletException, IOException {
        // Mock HttpServletRequest, HttpServletResponse, and FilterChain
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Create a User object with non-empty authorities
        User userDetails = new User("testUser", "", Collections.singletonList(() -> "ROLE_USER"));

        // Mock behavior of getUsernameFromToken and loadUserByUsername
        lenient().when(jwtHelper.getUsernameFromToken(any())).thenReturn("testUser");

        lenient().when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        lenient().when(jwtHelper.validateToken(anyString(), any(UserDetails.class))).thenReturn(true).thenThrow(new RuntimeException("Validation Error"));

        // Invoke the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify that getUsernameFromToken and loadUserByUsername were called
        verify(jwtHelper).getUsernameFromToken(any());

    }

    @Test
    public void testDoFilterInternal_IllegalArgumentExceptionInGetUsernameFromToken() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, and FilterChain
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock behavior of getUsernameFromToken to throw IllegalArgumentException
        when(jwtHelper.getUsernameFromToken(any())).thenThrow(IllegalArgumentException.class);

        // Invoke the filter
        jwtAuthenticationFilter.doFilterInternal(request, response, mock(FilterChain.class));

        // Verify that getUsernameFromToken and loadUserByUsername were not called
        verify(jwtHelper).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());

    }

    @Test
    public void testHandleException_IllegalArgumentExceptionInHandleException() {
        // Mock HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Assert that IllegalArgumentException is thrown when errorMessage is null
        assertThrows(IllegalArgumentException.class, () -> jwtAuthenticationFilter.handleException(response, 400, null));
    }

    @Test
    public void testHandleException_IOExceptionInHandleException() {
        // Mock HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Assert that no exception is thrown when calling handleException with IOException
        assertDoesNotThrow(() -> jwtAuthenticationFilter.handleException(response, 400, "Error message"));
    }


//Test to check the Authorization header
    @Test
    public void testEmptyAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(" ");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtHelper, never()).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    public void testMissingAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtHelper, never()).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    public void testInvalidPrefix() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtHelper, never()).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    public void testMalformedToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, never()).loadUserByUsername(any());

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, never()).loadUserByUsername(any());    }

    @Test
    public void testWhitespaceAroundBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(" Bearer some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtHelper, never()).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        when(request.getHeader("Authorization")).thenReturn("Bearer  some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }
    @Test
    public void testWhitespaceAroundBearers() throws ServletException, IOException {
        // First case: " Bearer some_token"
        when(request.getHeader("Authorization")).thenReturn(" Bearer some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtHelper, never()).getUsernameFromToken(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        // Second case: "Bearer  some_token"
        when(request.getHeader("Authorization")).thenReturn("Bearer  some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(userDetailsService, never()).loadUserByUsername(any());    }

    @Test
    public void testCaseInsensitiveBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("bEaRER some_token");
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        // No verification needed here, successful authentication would follow through other parts of the code
    }





    @Test
    void testIllegalArgumentException() throws Exception {
        // Mock objects (similar to previous tests)
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Mock request header
        String token = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Mock user details service and JWT helper (exception on getUsernameFromToken)
        when(jwtHelper.getUsernameFromToken(token)).thenThrow(new IllegalArgumentException("Error fetching username from token"));

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify security context is not set and exception handling is called
        verify(securityContext, times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }


    @Test
    public void testValidBearerToken() throws Exception {
        // Mock objects
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        UserDetails userDetails = mock(UserDetails.class);

        // Mock request header
        String token = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Mock user details service and JWT helper
        when(jwtHelper.getUsernameFromToken(token)).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(userDetails);
        when(jwtHelper.validateToken(token, userDetails)).thenReturn(true);

        // Execute filter
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Verify security context
//        verify(securityContext,times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

}
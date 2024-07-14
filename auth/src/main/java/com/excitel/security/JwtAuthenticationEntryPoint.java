package com.excitel.security;
import com.excitel.dto.FeignResponseDtO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
/**
 * Custom authentication entry point for JWT authentication.
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Invoked when authentication fails.
     *
     * @param request       The HTTP request.
     * @param response      The HTTP response.
     * @param authException The authentication exception.
     * @throws IOException      If an I/O error occurs.
     * @throws ServletException If an error occurs during servlet handling.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpStatus status = HttpStatus.valueOf(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
        response.setStatus(status.value());
        response.setContentType("application/json");
        FeignResponseDtO feignResponseDtO = new FeignResponseDtO(status, "Access Denied !! " , "Not Authorized","Not Authorized","Hacker","Access Denied");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(feignResponseDtO);
        response.getOutputStream().println(json);
    }
}

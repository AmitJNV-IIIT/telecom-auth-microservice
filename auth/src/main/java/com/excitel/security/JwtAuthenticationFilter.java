package com.excitel.security;
import com.excitel.dto.FeignResponseDtO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired//NOSONAR
    private JwtHelper jwtHelper;
    @Autowired//NOSONAR
    private UserDetailsService userDetailsService;

    /**
     * Performs the JWT authentication.
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain.
     * @throws ServletException If an error occurs during servlet handling.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        logger.info(" Header :  {}", requestHeader!=null);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            //looking good
            token = requestHeader.substring(7);
            try {username = this.jwtHelper.getUsernameFromToken(token);}
            catch (IllegalArgumentException e) {
                handleException(response, HttpServletResponse.SC_BAD_REQUEST, "Illegal Argument while fetching the username !!");}
        } else {
            logger.info("Invalid Header Value !! ");
            logger.info("No Bearer token found in Authorization header !!");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken != null) {
                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("Validation fails !!"); //NOSONAR
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Handles exceptions.
     *
     * @param response     The HTTP servlet response.
     * @param statusCode   The status code of the response.
     * @param errorMessage The error message.
     * @return ResponseEntity containing the error response.
     */
    public ResponseEntity<FeignResponseDtO> handleException(HttpServletResponse response,int statusCode, String errorMessage) {
        if (errorMessage==null) {
            throw new IllegalArgumentException("Error message cannot be null");
        }
        logger.debug("Handling exception with status code: {} and message: {}", statusCode, errorMessage);
        FeignResponseDtO feignResponseDtO = new FeignResponseDtO();
        feignResponseDtO.setStatus(HttpStatus.valueOf(statusCode));
        feignResponseDtO.setMessage(errorMessage);
        logger.debug("Returning FeignResponseDtO: {}", feignResponseDtO);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(feignResponseDtO);
    }

}
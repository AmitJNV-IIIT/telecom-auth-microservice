package com.excitel.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.management.ServiceNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.excitel.constant.AppConstants.INVALIDATE_TOKEN;
import static com.excitel.constant.AppConstants.JWT_TOKEN_VALIDITY;

/**
 * Helper class for JWT token generation, validation, and extraction.
 */
@Component
public class JwtHelper {

    @Autowired//NOSONAR
    private RedisTemplate<String,Object> redisTemplate;

    @Value("${SECRET-KEY}")
    private static String secret;
    private static final Logger logger = LoggerFactory.getLogger(JwtHelper.class);

    public JwtHelper(@Value("${SECRET-KEY}") String secret) {
        this.secret = secret;//NOSONAR
    }

    // Retrieve username from JWT token

    /**
     * Retrieves the username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from the JWT token.
     *
     * @param token The JWT token.
     * @return The expiration date of the token.
     */
    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    /**
     * Generic method to extract a claim from the provided JWT token using a claim resolver function.
     *
     * @param token The JWT token to extract the claim from.
     * @param claimsResolver A function that specifies how to extract the desired claim from the Claims object.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim value or null if not found.
     * @throws ExpiredJwtException If the token has expired.
     */
    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Parses the provided JWT token and retrieves all claims.
     * This method validates the token signature and throws an ExpiredJwtException if the token is expired.
     *
     * @param token The JWT token to parse.
     * @return All claims contained within the JWT token.
     * @throws ExpiredJwtException If the token has expired.
     * @throws MalformedJwtException If the token has an invalid format.
     * @throws SignatureException If the token signature is invalid.
     * @throws UnsupportedJwtException If the token uses an unsupported JWT algorithm.
     * @throws IllegalArgumentException If the token is null or empty.
     */
    // For retrieving any information from token we will need the secret key
    private static Claims getAllClaimsFromToken(String token) throws ExpiredJwtException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();//NOSONAR
        } catch (ExpiredJwtException exception) {//NOSONAR
            logger.info("Token Expired {}", exception.getMessage());
            throw exception; // Rethrow the exception to indicate token expiration
        }
    }

    // Check if the token has expired
    /**
     * Checks if the provided JWT token has expired based on its expiration claim.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token is expired, false otherwise.
     * @throws JwtException If there's an error parsing the token.
     */
    public static boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generate token for user
    /**
     * Generates a JWT token for a user containing the username, mobile number, and role claims.
     *
     * @param userDetails The UserDetails object representing the user.
     * @param mobileNumber The mobile number of the user.
     * @param role The user's role (e.g., "ADMIN", "USER").
     * @return The generated JWT token string.
     */
    public String generateToken(UserDetails userDetails, String mobileNumber, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("mobileNumber", mobileNumber); // Add mobile number as a claim
        claims.put("role", "ROLE_" + role);
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(JWT_TOKEN_VALIDITY.getValue()) * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)//NOSONAR
                .compact();
    }

    /**
     * Validates the JWT token against the provided user details.
     *
     * @param token The JWT token to validate.
     * @param userDetails The UserDetails object representing the user.
     * @return True if the token is valid for the user, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    /**
     * Extracts the mobile number from the JWT token.
     *
     * @param token The JWT token from which to extract the mobile number.
     * @return The mobile number extracted from the token.
     */    public String extractMobileNumberFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("mobileNumber"); // Assuming mobile number is stored in JWT claims
    }
    /**
     * Extracts the user's role information from a provided JWT token.
     *
     * This method assumes the role information is stored in a claim named "role" within the token.
     * If the claim is not found or the token is invalid, this method might return null.
     *
     * @param token The JWT token string to extract the role from.
     * @return The user's role as a String, or null if the role cannot be extracted.
     * @throws IllegalArgumentException if the provided token is null.
     */
    public String extractRoleFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("role");
    }
    /**
     * Marks the provided JWT token as invalid in the cache.
     *
     * This method attempts to retrieve the set of invalidated tokens from the cache using the key specified by `INVALIDATE_TOKEN.getValue()`.
     * If the set doesn't exist or `redisTemplate` is null, the method does nothing.
     * Otherwise, it adds the provided token to the set of invalidated tokens and stores the updated set back in the cache with an expiry time of 2 hours.
     *
     * @param token The JWT token string to invalidate.
     * @throws IllegalArgumentException if the provided token is null.
     * @throws ServiceNotFoundException if there's an error processing data with the cache (e.g., JsonProcessingException).
     */
    public void invalidateToken(String token) throws ServiceNotFoundException {
        if (token == null ) {
            throw new IllegalArgumentException("Token must not be null");
        }
        Set<String> inValidatedTokens = new HashSet<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (isNullOrNot()) {
                String object = (String) redisTemplate.opsForValue().get(INVALIDATE_TOKEN.getValue());
                inValidatedTokens = objectMapper.readValue(object, Set.class);
            }
            inValidatedTokens.add(token);
            String tokenJson = objectMapper.writeValueAsString(inValidatedTokens);
            redisTemplate.opsForValue().set(INVALIDATE_TOKEN.getValue(), tokenJson, 2, TimeUnit.HOURS);
        } catch (JsonProcessingException exception) {
            throw new ServiceNotFoundException(exception.getMessage());
        }
    }

    /**
     * Checks if the provided JWT token is marked as invalid in the cache.
     *
     * This method attempts to retrieve the set of invalidated tokens from the cache using the key specified by `INVALIDATE_TOKEN.getValue()`.
     * If the set doesn't exist or `redisTemplate` is null, the method assumes the token is valid and returns false.
     * Otherwise, it checks if the provided token exists within the set of invalidated tokens.
     *
     * @param token The JWT token string to check for validation.
     * @return True if the token is found in the set of invalidated tokens, false otherwise.
     * @throws IllegalArgumentException if the provided token is null.
     * @throws ServiceNotFoundException if there's an error processing data with the cache (e.g., JsonProcessingException, NullPointerException).
     */
    public boolean isTokenInvalidated(String token) throws ServiceNotFoundException {
        if (token == null ) {
            throw new IllegalArgumentException("Token must not be null");
        }
        try {
            Set<String> inValidatedTokens;
            ObjectMapper objectMapper = new ObjectMapper();
            if (isNullOrNot()) {
                String object = (String) redisTemplate.opsForValue().get(INVALIDATE_TOKEN.getValue());
                inValidatedTokens = objectMapper.readValue(object, Set.class);
                return inValidatedTokens.contains(token);
            }
            return false;
        } catch (JsonProcessingException | NullPointerException exception) {
            throw new ServiceNotFoundException(exception.getMessage());
        }
    }
    private boolean isNullOrNot() {
        return redisTemplate.opsForValue().get(INVALIDATE_TOKEN.getValue()) != null;
    }
}
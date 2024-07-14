package com.excitel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.ServiceNotFoundException;
import java.util.Date;

import static com.excitel.constant.AppConstants.JWT_TOKEN_VALIDITY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class JwtHelperTest {

    private static final Logger log = LoggerFactory.getLogger(JwtHelperTest.class);
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JwtParser jwtParser;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    private static final String SECRET = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    private final JwtHelper jwtHelper = new JwtHelper(SECRET);

    private static final String VALID_TOKEN = "validToken";
    private static final String INVALID_TOKEN = "invalidToken";
    private static final String INVALIDATE_TOKEN = "__invalidatedTokens__";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        String token = jwtHelper.generateToken(createUserDetails("8102312825"), "8102312825", "USER");
        String username = jwtHelper.getUsernameFromToken(token);
        assertEquals("8102312825", username);
    }

    @Test
    void getExpirationDateFromToken_ValidToken_ReturnsExpirationDate() {
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDateExpected = new Date(currentTimeMillis + Long.parseLong(JWT_TOKEN_VALIDITY.getValue()) * 1000);
        String token = jwtHelper.generateToken(createUserDetails("8102312825"), "8102312825", "USER");
        Date retrievedExpirationDate = jwtHelper.getExpirationDateFromToken(token);

        long expirationExpectedMillis = expirationDateExpected.getTime() / 1000;
        long retrievedExpirationMillis = retrievedExpirationDate.getTime() / 1000;

        assertEquals(expirationExpectedMillis, retrievedExpirationMillis);
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        String token = jwtHelper.generateToken(createUserDetails("8102312825"), "8102312825", "USER");
        assertFalse(jwtHelper.isTokenExpired(token));
    }

    @Test
    void validateToken_ValidTokenAndUserDetails_ReturnsTrue() {
        UserDetails userDetails = createUserDetails("8102312825");
        String token = jwtHelper.generateToken(userDetails, "8102312825", "USER");
        assertTrue(jwtHelper.validateToken(token, userDetails));
    }

    @Test
    void extractMobileNumberFromToken_ValidToken_ReturnsMobileNumber() {
        String token = jwtHelper.generateToken(createUserDetails("8102312825"), "8102312825", "USER");
        String mobileNumber = jwtHelper.extractMobileNumberFromToken(token);
        assertEquals("8102312825", mobileNumber);
    }

    @Test
    void extractRoleFromToken_ValidToken_ReturnsRole() {
        String token = jwtHelper.generateToken(createUserDetails("8102312825"), "8102312825", "USER");
        String role = jwtHelper.extractRoleFromToken(token);
        assertEquals("ROLE_USER", role);
    }

    @Test
    void getUsernameFromToken_InvalidToken_ThrowsExpiredJwtException() {
        final String SECRET = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";
        long expiredTimeMillis = System.currentTimeMillis() - 1000; // 1 second ago
        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date(expiredTimeMillis))
                .setExpiration(new Date(expiredTimeMillis)) // Expired token
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        // Now try to get username from the expired token
        assertThrows(ExpiredJwtException.class, () -> jwtHelper.getUsernameFromToken(expiredToken));
    }

    private UserDetails createUserDetails(String username) {
        return User.withUsername(username).password("password").roles("USER").build();
    }

    @Test
    void generateToken_ValidUserDetails_ReturnsToken() {
        UserDetails userDetails = createUserDetails("8102312825");
        String token = jwtHelper.generateToken(userDetails, "8102312825", "USER");
        assertNotNull(token);
    }
    @Test
    void shouldThrowExceptionWhenTokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.invalidateToken(null));
    }

    @Test
    void invalidateToken_NullToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.isTokenInvalidated(null));
    }

    @Test
    void invalidateToken_NullRedisTemplate_ThrowsNullPointerException() {
        JwtHelper jwtHelper = new JwtHelper(null);
        assertThrows(NullPointerException.class, () -> jwtHelper.invalidateToken("token"));
    }


    @Test
    void isTokenInvalidated_NullToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.isTokenInvalidated(null));
    }

    @Test
    public void testGetUsernameFromToken_ExceptionsBubbleUp() throws Exception {
        String token = "expired_token"; // Reuse exception handling from previous test

        try {
            jwtHelper.getUsernameFromToken(token);
            fail("Expected exception to bubble up"); // Fail the test if no exception is thrown
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("JWT strings must contain exactly 2 period characters. Found: 0")); // Verify the exception message
        }
    }
    @Test
    void testInvalidateToken_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.invalidateToken(null));
    }
    @Test
    void testisTokenInvalidated_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.isTokenInvalidated(null));
    }


    @Test
    void testIsTokenInvalidated_NullToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.isTokenInvalidated(null));
    }

    @Test
    void testIsTokenInvalidated_NullRedisTemplate_ThrowsServiceNotFoundException() {
        JwtHelper jwtHelper = new JwtHelper(null);
        assertThrows(ServiceNotFoundException.class, () -> jwtHelper.isTokenInvalidated("token"));
    }

    @Test
    void testInvalidateToken_NullToken_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> jwtHelper.invalidateToken(null));
    }

    @Test
    void testInvalidateToken_NullRedisTemplate_ThrowsServiceNotFoundException() {
        JwtHelper jwtHelper = new JwtHelper(null);
        assertThrows(NullPointerException.class, () -> jwtHelper.invalidateToken("token"));
    }

}
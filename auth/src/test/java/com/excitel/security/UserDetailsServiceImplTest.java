package com.excitel.security;

import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.model.Customer;
import com.excitel.redishelper.AuthRedis;
import com.excitel.repository.AuthDynamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private AuthDynamoRepository authDynamoRepository;

    @Mock
    private AuthRedis authRedis;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setMobileNumber("testUser");
        testCustomer.setPassword("testPassword");
        testCustomer.setRole("ROLE_USER");
    }

    @Test
    void testLoadUserByUsername_SuccessfulCacheRetrieval() {
        // Arrange
        when(authRedis.getUserCredDetail(any())).thenReturn(mock(UserDetails.class));

        // Act
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        verify(authRedis, times(1)).getUserCredDetail(any());
        verify(authDynamoRepository, never()).getUserByMobileNumber(any(), any());
    }

    @Test
    void testLoadUserByUsername_SuccessfulDynamoDBRetrieval() {
        // Arrange
        when(authRedis.getUserCredDetail(any())).thenReturn(null);
        when(authDynamoRepository.getUserByMobileNumber(any(), any())).thenReturn(testCustomer);

        // Act
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        verify(authRedis, times(1)).getUserCredDetail(any());
        verify(authDynamoRepository, times(1)).getUserByMobileNumber(any(), any());
        verify(authRedis, times(1)).addUserCredCache(any(), any());
    }

    @Test
    void testLoadUserByUsername_CustomAuthenticationException() {
        // Arrange
        when(authRedis.getUserCredDetail(any())).thenReturn(null);
        when(authDynamoRepository.getUserByMobileNumber(any(), any())).thenReturn(null);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> userDetailsServiceImpl.loadUserByUsername("nonExistentUser"));
        verify(authRedis, times(1)).getUserCredDetail(any());
        verify(authDynamoRepository, times(1)).getUserByMobileNumber(any(), any());
    }

    @Test
    void testLoadUserByUsername_ResourceNotFoundException() {
        // Arrange
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(ResourceNotFoundException.builder().build());

        // Act & Assert
        assertThrows(CustomDynamoDbException.class, () -> userDetailsServiceImpl.loadUserByUsername("username"));
    }

    @Test
    void testLoadUserByUsername_ProvisionedThroughputExceededException() {
        // Arrange
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(ProvisionedThroughputExceededException.builder().build());

        // Act & Assert
        assertThrows(CustomDynamoDbException.class, () -> userDetailsServiceImpl.loadUserByUsername("username"));
    }


    @Test
    void testLoadUserByUsername_DynamoDbException() {
        // Arrange
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(DynamoDbException.builder().build());

        // Act & Assert
        assertThrows(CustomDynamoDbException.class, () -> userDetailsServiceImpl.loadUserByUsername("username"));
    }

    // Similar tests for other exceptions and scenarios...
}

//    void loadUserByUsername_Success() {
//        // Arrange
//        Customer user = new Customer();
//        user.setMobileNumber("username");
//        user.setPassword("password");
//        user.setRole("ROLE_USER");
//
//
//        when(authDynamoRepository.getUserByMobileNumber("username", Arrays.asList("Password", "UserRole"))).thenReturn(user);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername("username");
//
//        assertNotNull(userDetails);
//        assertEquals("username", userDetails.getUsername());
//        assertEquals("password", userDetails.getPassword());
//        assertEquals(1, userDetails.getAuthorities().size());
//        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
//    }

//    @Test
//    void loadUserByUsername_ResourceNotFoundException() {
//
//        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(ResourceNotFoundException.builder().build());
//
//        assertThrows(CustomDynamoDbException.class, () -> userDetailsService.loadUserByUsername("username"));
//    }
//
//    @Test
//    void loadUserByUsername_ProvisionedThroughputExceededException() {
//        // Arrange
//        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(ProvisionedThroughputExceededException.builder().build());
//
//        // Act & Assert
//        assertThrows(CustomDynamoDbException.class, () -> userDetailsService.loadUserByUsername("username"));
//    }
//
//    @Test
//    void loadUserByUsername_SdkClientException() {
//        // Arrange
//        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(SdkClientException.builder().build());
//
//        // Act & Assert
//        assertThrows(CustomDynamoDbException.class, () -> userDetailsService.loadUserByUsername("username"));
//    }
//
//    @Test
//    void loadUserByUsername_DynamoDbException() {
//        // Arrange
//        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenThrow(DynamoDbException.builder().build());
//
//        // Act & Assert
//        assertThrows(CustomDynamoDbException.class, () -> userDetailsService.loadUserByUsername("username"));
//    }





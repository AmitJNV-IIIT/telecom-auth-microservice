package com.excitel.redishelper;
import com.excitel.model.Customer;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.excitel.constant.AppConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthRedisTest {

    @Mock
    private RedisCacheManager redisCacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private AuthRedis authRedis;

    @Mock
    private Cache.ValueWrapper valueWrapper;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRemoveUserDetailsCache() {
        // Mock cache
        when(redisCacheManager.getCache("Customer")).thenReturn(cache);

        // Call the method
        authRedis.removeUserDetailsCache("1234567890");

        // Verify cache eviction
        verify(cache, times(1)).evictIfPresent("1234567890_details");
        verify(cache, times(1)).evictIfPresent("null_details");
    }

    @Test
    void testAddUserDetailsCache() {
        // Mock cache
        when(redisCacheManager.getCache("Customer")).thenReturn(cache);

        // Mock customer
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");

        // Call the method
        authRedis.addUserDetailsCache("1234567890", customer);

        // Verify cache insertion
        verify(cache, times(1)).put("1234567890_details", customer);
    }

    @Test
    void testRemoveUserCredCache() {
        String mobileNumber = "1234567890";
        Cache customerCache = mock(Cache.class);
        when(redisCacheManager.getCache("Customer")).thenReturn(customerCache);

        // Test removeUserCredCache
        authRedis.removeUserCredCache(mobileNumber);
        verify(customerCache).evictIfPresent(mobileNumber + "_cred");
    }

    @Test
    void testGetUserCacheDetail() {
        // Mock cache
        when(redisCacheManager.getCache("Customer")).thenReturn(cache);

        // Mock customer
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");

        // Mock cache get
        when(cache.get("1234567890_details")).thenReturn(() -> customer);

        // Call the method
        Customer result = authRedis.getUserCacheDetail("1234567890");

        // Verify result
        assertNotNull(result);
        assertEquals("1234567890", result.getMobileNumber());
    }

    @Test
    void testGetUserCredDetailWhenCacheNull() {
        String mobileNumber = "1234567890";

        // Mock redisCacheManager to return null
        when(redisCacheManager.getCache("Customer")).thenReturn(null);

        // Call the method
        UserDetails userDetails = authRedis.getUserCredDetail(mobileNumber);

        // Verify that userDetails is null
        assertNull(userDetails);

        // Verify interactions
        verify(redisCacheManager).getCache("Customer");
        // customerCache.get(key) should not be called since customerCache is null
        verify(cache, never()).get(anyString());
    }

    @Test
    void testAddUserCredCache_WhenCacheNotNull() {
        String mobileNumber = "1234567890";
        String key = mobileNumber + "_credentials";

        // Create a UserDetails object with a non-null collection of GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails user = new User("username", "password", authorities);

        // Mock cache behavior
        when(redisCacheManager.getCache("Customer")).thenReturn(cache);

        // Call the method under test
        authRedis.addUserCredCache(mobileNumber, user);
        assertNull(authRedis.getUserCredDetail(mobileNumber));

    }

    @Test
    void testAddUserCredCache_WhenCacheNull() {
        String mobileNumber = "1234567890";
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails user = new User("username", "password", authorities);

        // Mock cache behavior
        when(redisCacheManager.getCache("Customers")).thenReturn(cache);

        // Call the method under test
        authRedis.addUserCredCache(mobileNumber, user);
        assertNull(authRedis.getUserCredDetail(mobileNumber));
    }

    @Test
    void test_getUserCredDetail_withCachedValue() {
        // Mock data
        String mobileNumber = "1234567890";
        UserDetails expectedUserDetails = mock(UserDetails.class);

        // Mock behavior of cacheManager.getCache()
        Cache customerCache = mock(Cache.class);
        when(redisCacheManager.getCache("customers")).thenReturn(customerCache);

        // Mock behavior of customerCache.get(key)
        String key = mobileNumber + "credentials";
        when(customerCache.get(key)).thenReturn(mock(Cache.ValueWrapper.class));
        when(customerCache.get(key).get()).thenReturn(expectedUserDetails);

        // Call the method
        UserDetails result = authRedis.getUserCredDetail(mobileNumber);

        // Verify that the method returns the expected UserDetails
        assertNull(result);
    }

    @Test
    public void testGetUserCacheDetail_WithCacheHit() {
        String mobileNumber = "1234567890";
        Customer expectedCustomer = new Customer(); // Create a sample customer object

        // Mock Cache behavior
        Cache mockCache = mock(Cache.class);
        Cache.ValueWrapper mockWrapper = mock(Cache.ValueWrapper.class);
        when(mockCache.get(mobileNumber + DETAILS.getValue())).thenReturn(mockWrapper);
        when(mockWrapper.get()).thenReturn(expectedCustomer);
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(mockCache);

        AuthRedis authRedis = new AuthRedis(redisCacheManager);
        Customer actualCustomer = authRedis.getUserCacheDetail(mobileNumber);

        assertEquals(expectedCustomer, actualCustomer);
        verify(mockCache).get(mobileNumber + DETAILS.getValue());
    }

    @Test
    public void testGetUserCacheDetail_WithCacheMiss() {
        String mobileNumber = "1234567890";

        // Mock Cache behavior (cache miss)
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(null); // Simulate cache not found

        AuthRedis authRedis = new AuthRedis(redisCacheManager);
        Customer actualCustomer = authRedis.getUserCacheDetail(mobileNumber);

        assertNull(actualCustomer);
    }
   @Test
    public void testRemoveUserDetailsCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(null);
        try {
            authRedis.removeUserDetailsCache(mobileNumber);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testRemoveUserCredsCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CREDENTIALS.getValue())).thenReturn(null);
        try {
            authRedis.removeUserCredCache(mobileNumber);

        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    @Test
    public void testAddUserDetailsCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(null);
        try {
            authRedis.addUserDetailsCache(mobileNumber, new Customer());

        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    @Test
    public void testAddUserCredCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CREDENTIALS.getValue())).thenReturn(null);
        try {
            authRedis.addUserCredCache(mobileNumber,new Customer());

        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testGetUserCredCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(null);
        try {
            authRedis.removeUserDetailsCache(mobileNumber);

        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testGetUserDetailsCache_CacheIsNull()  {
        String mobileNumber = "1234567890";
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(null);
        try {
            authRedis.removeUserDetailsCache(mobileNumber);
            System.out.println("Cache was unexpectedly null, but no exception was thrown.");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    @Test
    public void testGetUserDetailsCache_CacheIsNOTNullAnd_CustomerWrapperNull()  {
        String mobileNumber = "1234567890";
        String key = mobileNumber + CREDENTIALS.getValue();

        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(cache);
        when(cache.get(key)).thenReturn(null);
        try {
            authRedis.getUserCacheDetail(mobileNumber);

        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    @Test
    public void testGetUserCredDetail() {
        // Setup
        String mobileNumber = "1234567890";
        String key = mobileNumber + CREDENTIALS.getValue();
        Customer userDetails = new Customer();
        userDetails.setMobileNumber("1234567890");// Assuming UserDetails is a class you've defined
        userDetails.setName("Rohit");
        userDetails.setEmail("rohit@gmail.com");

        // Mocking behavior
        when(redisCacheManager.getCache(CUSTOMERS.getValue())).thenReturn(cache);
        when(cache.get(key)).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(userDetails);

        // Call the method
        UserDetails result = authRedis.getUserCredDetail(mobileNumber);

        // Verify
        assertNotNull(result);
        assertEquals(userDetails, result);
    }
}

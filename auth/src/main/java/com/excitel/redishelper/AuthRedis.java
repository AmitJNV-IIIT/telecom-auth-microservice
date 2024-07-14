package com.excitel.redishelper;

import com.excitel.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.excitel.constant.AppConstants.*;
/**
 * This class provides methods for caching and evicting user details and credentials in Redis.
 * It relies on a {@link RedisCacheManager} to interact with the cache.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@Component
public class AuthRedis {

    private final RedisCacheManager redisCacheManager;

    public AuthRedis (RedisCacheManager redisCacheManager){
        this.redisCacheManager = redisCacheManager;
    }


    /**
     * Removes cached user details for the given mobile number from the "CUSTOMERS" cache.
     * This method also evicts the "null_details" key if present.
     *
     * @param mobileNumber The mobile number of the user for which to remove cached details.
     */
    public void removeUserDetailsCache(String mobileNumber){
        String key = mobileNumber + DETAILS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if(customerCache != null) {
            customerCache.evictIfPresent(key);
            customerCache.evictIfPresent("null_details");
        }
    }
    /**
     * Adds user details (represented by a {@link Customer} object) to the "CUSTOMERS" cache
     * with a key formed by the mobile number and the "DETAILS" constant suffix.
     *
     * @param mobileNumber The mobile number of the user.
     * @param customer The {@link Customer} object containing user details to cache.
     */
    public void addUserDetailsCache(String mobileNumber, Customer customer){
        String key = mobileNumber + DETAILS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if( customerCache != null){
            customerCache.put(key, customer);
        }
    }
    /**
     * Removes cached user credentials for the given mobile number from the "CUSTOMERS" cache.
     *
     * @param mobileNumber The mobile number of the user for which to remove cached credentials.
     */
    public void removeUserCredCache(String mobileNumber){
        String key = mobileNumber + CREDENTIALS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if(customerCache != null){
        customerCache.evictIfPresent(key);
        }
    }
    /**
     * Adds user credentials (represented by a {@link UserDetails} object) to the "CUSTOMERS" cache
     * with a key formed by the mobile number and the "CREDENTIALS" constant suffix.
     *
     * @param mobileNumber The mobile number of the user.
     * @param user The {@link UserDetails} object containing user credentials to cache.
     */
    public void addUserCredCache(String mobileNumber, UserDetails user){
        String key = mobileNumber + CREDENTIALS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if (customerCache != null) {
            customerCache.put(key, user);
        }
    }
    /**
     * Retrieves cached user details (as a {@link Customer} object) for the given mobile number
     * from the "CUSTOMERS" cache.
     *
     * @param mobileNumber The mobile number of the user for which to retrieve cached details.
     * @return A {@link Customer} object containing cached user details or null if not found.
     */
    public Customer getUserCacheDetail(String mobileNumber) {
        String key = mobileNumber + DETAILS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if (customerCache != null) {
            Cache.ValueWrapper cachedCustomerWrapper = customerCache.get(key);
            if (cachedCustomerWrapper != null) {
                return (Customer) cachedCustomerWrapper.get();
            }
        }
        return null;
    }
    /**
     * Retrieves cached user credentials (as a {@link UserDetails} object) for the given mobile number
     * from the "CUSTOMERS" cache.
     *
     * @param mobileNumber The mobile number of the user for which to retrieve cached credentials.
     * @return A {@link UserDetails} object containing cached user credentials or null if not found.
     */

    public UserDetails getUserCredDetail(String mobileNumber) {
        String key = mobileNumber + CREDENTIALS.getValue();
        Cache customerCache = redisCacheManager.getCache(CUSTOMERS.getValue());
        if(customerCache != null && customerCache.get(key)!=null){
                Cache.ValueWrapper cachedCustomerWrapper = customerCache.get(key);
                return (UserDetails) cachedCustomerWrapper.get();
            }
        return null;
    }
}

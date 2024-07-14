package com.excitel.security;
import com.excitel.exception.custom.CustomAuthenticationException;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.model.Customer;
import com.excitel.redishelper.AuthRedis;
import com.excitel.repository.AuthDynamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

import static com.excitel.constant.AppConstants.PASSWORD;
import static com.excitel.constant.AppConstants.USER_ROLE;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired//NOSONAR
    private AuthDynamoRepository authDynamoRepository;
    @Autowired //NOSONAR
    private AuthRedis authRedis;
    @Value("${pass}")
    private String put;

    /**
     * Loads user details by username from DynamoDB.
     *
     * @param username The username to load user details for.
     * @return UserDetails object containing user details.
     * @throws UsernameNotFoundException if the user is not found.
     * @throws CustomDynamoDbException   if there is an issue with DynamoDB.
     * @throws CustomAuthenticationException if there is an authentication issue.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {try {

            UserDetails cache = authRedis.getUserCredDetail(username);
            if(cache!=null) return cache;
            log.info("cache exists");
            Customer user = authDynamoRepository.getUserByMobileNumber(
                    username,Arrays.asList(PASSWORD.getValue(), USER_ROLE.getValue()));
            String pass = user.getPassword();
            UserDetails customer = new User(
                    username,
                    pass,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole())
            );
            authRedis.addUserCredCache(username, customer);
            return customer;
        }catch (ResourceNotFoundException e) {
            throw new CustomDynamoDbException("Resource not found: " + e.getMessage());
        } catch (ProvisionedThroughputExceededException e) {
            throw new CustomDynamoDbException("Provisioned throughput exceeded: " + e.getMessage());
        }  catch (DynamoDbException e) {
            throw new CustomDynamoDbException("DynamoDB exception: " + e.getMessage());
        } catch (Exception ex){
            throw new CustomAuthenticationException(ex.getMessage());
        }
    }

}


package com.excitel.serviceimpl;//NOSONAR

import com.excitel.dto.FeignResponseDtO;
import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.exception.custom.CustomAuthenticationException;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.exception.custom.UserServiceException;
import com.excitel.model.Customer;
import com.excitel.redishelper.AuthRedis;
import com.excitel.repository.AuthDynamoRepository;
import com.excitel.security.JwtHelper;
import com.excitel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.ServiceNotFoundException;
import java.util.Arrays;

/**
 * Implementation of UserService for handling user-related operations.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordDecoder passwordDecoder;
    @Autowired//NOSONAR
    private AuthenticationManager authenticationManager;

    @Autowired//NOSONAR
    private JwtHelper jwtHelper;

    @Autowired//NOSONAR
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired//NOSONAR
    AuthDynamoRepository authDynamoRepository;

    @Autowired
    private AuthRedis authRedis;

    private String inavlidatedToken = "Token Invalidated";
    private String email = "Email";

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Validates the user token.
     *
     * @param token The user token to validate.
     * @return ResponseEntity containing the validation result.
     */

    @Override
    public ResponseEntity<FeignResponseDtO> validation(String token) throws ServiceNotFoundException {
        if(jwtHelper.isTokenInvalidated(token.substring(7)))
        {
            throw new UserNotFoundException(inavlidatedToken);

        }
        String mobileNumber = jwtHelper.extractMobileNumberFromToken(token.substring(7));
        String role = jwtHelper.extractRoleFromToken(token.substring(7)).substring(5);
        try {
            //get cached details if found return it
            Customer cacheDetail = authRedis.getUserCacheDetail(mobileNumber);
            if(cacheDetail!=null) {
                FeignResponseDtO cacheResponse = new FeignResponseDtO(
                        HttpStatus.OK,
                        "User authorised",
                        mobileNumber,
                        cacheDetail.getEmail(),
                        role,
                        cacheDetail.getSimType());
                return ResponseEntity.status(HttpStatus.OK).body(cacheResponse);
            }

            //if cache condition fails return
            Customer customer = authDynamoRepository.getUserByMobileNumber(mobileNumber, Arrays.asList("MobileNumber", email, "PlanType","SIMType"));//NOSONAR
            customer.setRole(role);
            FeignResponseDtO response = new FeignResponseDtO(
                    HttpStatus.OK,
                    "User authorised",
                    mobileNumber,
                    customer.getEmail(),
                    role,
                    customer.getSimType());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch(Exception e) {
            throw new CustomAuthenticationException(e.getMessage());
        }
    }
    /**
     * Logs in the user.
     *
     * @param mobileNumber The user's mobile number.
     * @param password     The user's password.
     * @return ResponseEntity containing the login result.
     */
    @Override
    public ResponseEntity<ResponseDTO> login(String mobileNumber, String password){
        try {
        String decodedPassword = passwordDecoder.decodePassword(password);
            return authenticateUser(mobileNumber, decodedPassword);
        } catch (AuthenticationException exception) {
            throw new CustomAuthenticationException("Invalid request body");
        }
    }

    @Override
    public Customer getUserDetail(String token) throws ServiceNotFoundException {
        if(jwtHelper.isTokenInvalidated(token.substring(7)))
        {
            logger.info("here I am");
            throw new UserNotFoundException(inavlidatedToken);
        }
        String mobileNumber = jwtHelper.extractMobileNumberFromToken(token.substring(7));
        //return cache if found else continue
        Customer cache = authRedis.getUserCacheDetail(mobileNumber);
        if(cache!=null) return cache;
        Customer customer = authDynamoRepository.getUserByMobileNumber(mobileNumber,
                Arrays.asList("MobileNumber", email, "SIMType", "UserRole", "Address", "PINCode", "Country", "CustomerName", "CustomerState"));
        //add user details in cache
        authRedis.addUserDetailsCache(mobileNumber,customer);
        return customer;
    }

    @Override
    public Customer updateUserDetail(Customer customer) {
        String mobileNumber = customer.getMobileNumber();
        authRedis.removeUserDetailsCache(mobileNumber);
        return authDynamoRepository.updateUserByMobileNumber(customer);
    }

    @Override
    public Customer loadUserByUsername(String username) {
        return null;
    }

    @Override
    public boolean resetPassword(String token, String oldPassword, String newPassword) throws ServiceNotFoundException {

        oldPassword = passwordDecoder.decodePassword(oldPassword);
        newPassword = passwordDecoder.decodePassword(newPassword);
        String mobileNumber = jwtHelper.extractMobileNumberFromToken(token.substring(7));
        if(jwtHelper.isTokenInvalidated(token.substring(7)))
        {
            throw new UserNotFoundException(inavlidatedToken);
        }
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(mobileNumber, oldPassword);
            String newEncryptedPassword = bCryptPasswordEncoder.encode(newPassword);
            authenticationManager.authenticate(authentication);
            authDynamoRepository.updatePassword(mobileNumber,newEncryptedPassword);
            return true;
        }catch (AuthenticationException ex){
            throw new CustomAuthenticationException(ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> logout(String token) throws ServiceNotFoundException {
        String mobileNumber = jwtHelper.extractMobileNumberFromToken(token.substring(7));
        if(jwtHelper.isTokenInvalidated(token.substring(7)))
        {
            authRedis.removeUserCredCache(mobileNumber);
            ResponseDTO response = new ResponseDTO(HttpStatus.UNAUTHORIZED,
                    "Invalid Auth Token",
                    "Cannot Logout");
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            jwtHelper.invalidateToken(token.substring(7));
            authRedis.removeUserDetailsCache(mobileNumber);
            authRedis.removeUserCredCache(mobileNumber);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
                    HttpStatus.OK,
                    "valid User",
                    "Successfully logged out"
            ));}
        catch(UserNotFoundException exception){
            throw  new UserNotFoundException("The token provided is not a valid token"+exception.getMessage());
        }catch (UserServiceException exception){
            throw new UserServiceException("INTERNAL:_SERVER_ERROR"+exception.getMessage());
        }
    }

    /**
     * Authenticates the user.
     *
     * @param mobileNumber The user's mobile number.
     * @param password     The user's password.
     * @return ResponseEntity containing the authentication result.
     */
    ResponseEntity<ResponseDTO> authenticateUser(String mobileNumber, String password){
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mobileNumber,
                password
        );
        Authentication authenticatedUser = authenticationManager.authenticate(authentication);
        Customer user = authDynamoRepository.getUserByMobileNumber(mobileNumber, Arrays.asList("SIMType", email, "UserRole"));
        UserDetails userDetails = new User(authenticatedUser.getName(), "", authenticatedUser.getAuthorities());
        String role = user.getRole();
        logger.info("User role {}",role);
        String token = jwtHelper.generateToken(userDetails, mobileNumber,role);
        ResponseDTO responseDtO = convertDtOJWT(
                HttpStatus.OK,
                role,
                "Auth_Token: "+token );
        return ResponseEntity.status(HttpStatus.OK).body(responseDtO);
    }
    /**
     * Converts DTO to JWT.
     *
     * @param status  The status of the response.
     * @param message The message of the response.
     * @param token   The token to be included in the response.
     * @return ResponseDTO containing the converted JWT.
     */
    private ResponseDTO convertDtOJWT(HttpStatus status, String message, String token){
        ResponseDTO jwtDtO = new ResponseDTO();
        jwtDtO.setStatus(status);
        jwtDtO.setMessage(message);
        jwtDtO.setResponse(token);
        return jwtDtO;
    }
}

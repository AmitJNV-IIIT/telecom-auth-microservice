package com.excitel.serviceimpl;

import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.model.Customer;
import com.excitel.model.OtpDB;
import com.excitel.redishelper.AuthRedis;
import com.excitel.redishelper.OTPRedisHelper;
import com.excitel.repository.AuthDynamoRepository;
import com.excitel.service.OTPService;
import com.excitel.service.PasswordService;
import io.lettuce.core.RedisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import static com.excitel.constant.AppConstants.*;

/**
 * Implementation of PasswordService for handling password-related operations.
 */
@Service
public class PasswordServiceImpl implements PasswordService {

    @Autowired//NOSONAR
    private PasswordDecoder passwordDecoder;

    @Autowired//NOSONAR
    private OTPRedisHelper otpRedisHelper;

    @Autowired//NOSONAR
    private PasswordEncoder passwordEncoder;

    @Autowired//NOSONAR
    private OTPService otpService;

    @Autowired//NOSONAR
    private AuthDynamoRepository authDynamoRepository;

    @Autowired
    private AuthRedis authRedis;

    private ResponseDTO createDTO(HttpStatus status, String response, String message) {
        return new ResponseDTO(status, response, message) ;
    }
    private final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    private final SecureRandom random = new SecureRandom();
    @Value("${AUTH_TOKEN}")
    private String authToken;
    private  final RestTemplate restTemplate = new RestTemplate();
    private String generateClientID(){
        return generateRandomString(CLIENT_ID_CHARS.getValue(), Integer.parseInt(CLIENT_ID_LENGTH.getValue()));
    }
    private String generateHashKey(){
        return generateRandomString(HASH_KEY_CHARS.getValue(), Integer.parseInt(HASH_KEY_LENGTH.getValue()));
    }
    private String generateRandomString(String chars, int length) {

        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            stringBuilder.append(chars.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }
    @Override
    public ResponseEntity<ResponseDTO> forgotPassword(String mobileNumber){
        try {
            Customer user = authDynamoRepository.getUserByMobileNumber(mobileNumber,
                    Arrays.asList(MOBILE.getValue(),EMAIL.getValue()));
            if(user == null) {
                ResponseDTO responseDtO = createDTO(HttpStatus.UNAUTHORIZED, "Invalid Mobile Number", "You are not a registered user");
                logger.error("NOT A REGISTERED USER!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDtO);
            }
            return sendOTP(mobileNumber);
        }catch(UserNotFoundException exception) {
            ResponseDTO responseDTO = createDTO(HttpStatus.INTERNAL_SERVER_ERROR, ERROR.getValue(), "You are not a registered user. " + exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
    // This method sends OTP to mobileNumber. To do: Send message as parameter.
    @Override
    public ResponseEntity<ResponseDTO>sendOTP(String mobileNumber) {
        ResponseEntity<String> response = null;
        logger.info("{} {} {} {}",ACCOUNT_SID.getValue(),FROM_NUMBER.getValue(),ACCOUNT_SID.getValue(),"HELLO");
        try {
            String otp = otpService.generateOTP();
            otpService.storeOTP(mobileNumber, otp);
            // To-Do: Move out the API call to a seperate function.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(ACCOUNT_SID.getValue(), authToken);
            String requestBody = "From=" + FROM_NUMBER.getValue() +
                    "&To=91" + mobileNumber +
                    "&Body=Your Excitel OTP is " + otp;
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            String twilioUrl = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID.getValue() + "/Messages.json";
            response = restTemplate.postForEntity(twilioUrl, request, String.class);
            logger.info("Twilio Response :{}" ,response.getBody());
            ResponseDTO responseDtO = createDTO(HttpStatus.OK,
                    "Success",
                    "OTP Send to the registered number");
            return ResponseEntity.status(HttpStatus.OK).body(responseDtO);
        }catch (Exception exception){
            logger.error(exception.getMessage());
            ResponseDTO responseDTO = createDTO(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), "Failed to send OTP");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> verifyOTP(String mobileNumber, String otp) {
        try{
            String isVerified = otpService.verifyOTP(mobileNumber, otp);
            if (isVerified.equals("verified")) {
                String keyAndHash = generateKeyAndHash(mobileNumber);
                ResponseDTO responseDtO = createDTO(HttpStatus.OK, keyAndHash, "OTP Verified");
                return ResponseEntity.status(HttpStatus.OK).body(responseDtO);
            } else if (isVerified.equals("otp expired")) {
                ResponseDTO responseDtO = createDTO(HttpStatus.BAD_REQUEST, "OTP Expired", "Resend OTP");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDtO);
            }
            ResponseDTO responseDtO = createDTO(HttpStatus.UNAUTHORIZED, "BAD CREDENTIALS", "Wrong OTP Provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDtO);

        }
        catch (Exception exception){
            ResponseDTO responseDtO = createDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Service Down",
                    "TRY AFTER SOMETIME");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDtO);
        }
    }
    @Override
    public String generateKeyAndHash(String mobileNumber) throws ServiceNotFoundException {
        return otpService.storeOTPWithClientID(mobileNumber,generateClientID(),generateHashKey());
    }
    @Override
    public boolean updatePassword(String newPassword, String mobileNumber, String keyAndHash) throws IOException {

        try {
            //decode hashed password : Uncomment Only During demo
            newPassword = passwordDecoder.decodePassword(newPassword);

            String[] parts = keyAndHash.split("=|:"); //NOSONAR
            String clientID = parts[1].trim();
            String hashValue = parts[2].trim();
            OtpDB otpDB = otpRedisHelper.getOtp(mobileNumber);

            if (Objects.equals(otpDB.getHashKey(), hashValue)
                    && Objects.equals(otpDB.getClientID(), clientID)) {
                authDynamoRepository.updatePassword(mobileNumber, passwordEncoder.encode(newPassword));
                authRedis.removeUserCredCache(mobileNumber);
                otpRedisHelper.removeOTP(mobileNumber);
                logger.info("Has verify");
                return true;
            } else {
                return false;
            }
        }catch (RedisConnectionException exception){
            throw new RedisConnectionException(exception.getMessage());
        }
    }
}

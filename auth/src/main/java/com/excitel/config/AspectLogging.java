package com.excitel.config;
import com.excitel.exception.custom.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))")
    public void controllerAndServiceImplMethods() {}

    @Pointcut("execution(@org.springframework.web.bind.annotation.GetMapping * com.excitel.controller..*.*(..))")
    public void getMappingMethods() {}

    //    @Pointcut("execution(@org.springframework.web.bind.annotation.PostMapping * com.excitel.controller..*.*(..))")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingMethods() {}

    //    @Pointcut("execution(@org.springframework.web.bind.annotation.PutMapping * com.excitel.controller..*.*(..))")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMappingMethods() {}

    //    @Pointcut("execution(@org.springframework.web.bind.annotation.DeleteMapping * com.excitel.controller..*.*(..))")
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMappingMethods() {}

    // Advice for @GetMapping methods
    @Before("getMappingMethods()")
    public void logGetRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("GET request to method: {}", methodName);
    }

    @Before("postMappingMethods()")
    public void logPostRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("POST request to method: {}", methodName);
    }

    @Before("putMappingMethods()")
    public void logPutRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("PUT request to method: {}", methodName);
    }

    @Before("deleteMappingMethods()")
    public void logDeleteRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("DELETE request to method: {}", methodName);
    }

    @Before("controllerAndServiceImplMethods()")
    public void logControllerMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Calling method: {}", methodName);
    }

    @After("getMappingMethods()")
    public void afterGetRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed GET request to method: {}", methodName);
    }

    @After("postMappingMethods()")
    public void afterPostRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed POST request to method: {}", methodName);
    }

    @After("putMappingMethods()")
    public void afterPutRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed PUT request to method: {}", methodName);
    }

    @After("deleteMappingMethods()")
    public void afterDeleteRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed DELETE request to method: {}", methodName);
    }

    @After("controllerAndServiceImplMethods()")
    public void afterControllerMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed call to method: {}", methodName);
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, CustomDynamoDbException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Error occured in database connection inside method: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, CustomerAlreadyExistsException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Phone number already exists error occured in: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, CustomAuthenticationException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Error in authentication: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, RegistrationFailedException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Registration error: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, InvalidRequestBodyException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Invalid request body: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, UserNotFoundException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("User not found: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, UserServiceException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("User Service error: {}, with message: {}", methodName, exception.getMessage());
    }

}
package com.excitel.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.excitel.exception.custom.CustomAuthenticationException;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.exception.custom.CustomerAlreadyExistsException;
import com.excitel.exception.custom.InvalidRequestBodyException;
import com.excitel.exception.custom.RegistrationFailedException;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.exception.custom.UserServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {AspectLogging.class})
@ExtendWith(SpringExtension.class)
class AspectLoggingTest {
    @Autowired
    private AspectLogging aspectLogging;

    /**
     * Method under test: {@link AspectLogging#logGetRequest(JoinPoint)}
     */
    @Test
    void testLogGetRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logGetRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logGetRequest(JoinPoint)}
     */
    @Test
    void testLogGetRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logGetRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logPostRequest(JoinPoint)}
     */
    @Test
    void testLogPostRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logPostRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logPostRequest(JoinPoint)}
     */
    @Test
    void testLogPostRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logPostRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logPutRequest(JoinPoint)}
     */
    @Test
    void testLogPutRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logPutRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logPutRequest(JoinPoint)}
     */
    @Test
    void testLogPutRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logPutRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logDeleteRequest(JoinPoint)}
     */
    @Test
    void testLogDeleteRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logDeleteRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logDeleteRequest(JoinPoint)}
     */
    @Test
    void testLogDeleteRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logDeleteRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logControllerMethodCall(JoinPoint)}
     */
    @Test
    void testLogControllerMethodCall() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logControllerMethodCall(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#logControllerMethodCall(JoinPoint)}
     */
    @Test
    void testLogControllerMethodCall2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logControllerMethodCall(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterGetRequest(JoinPoint)}
     */
    @Test
    void testAfterGetRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.afterGetRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterGetRequest(JoinPoint)}
     */
    @Test
    void testAfterGetRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.afterGetRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterPostRequest(JoinPoint)}
     */
    @Test
    void testAfterPostRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.afterPostRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterPostRequest(JoinPoint)}
     */
    @Test
    void testAfterPostRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.afterPostRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterPutRequest(JoinPoint)}
     */
    @Test
    void testAfterPutRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.afterPutRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterPutRequest(JoinPoint)}
     */
    @Test
    void testAfterPutRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.afterPutRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterDeleteRequest(JoinPoint)}
     */
    @Test
    void testAfterDeleteRequest() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.afterDeleteRequest(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterDeleteRequest(JoinPoint)}
     */
    @Test
    void testAfterDeleteRequest2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.afterDeleteRequest(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterControllerMethodCall(JoinPoint)}
     */
    @Test
    void testAfterControllerMethodCall() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.afterControllerMethodCall(joinPoint);

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test: {@link AspectLogging#afterControllerMethodCall(JoinPoint)}
     */
    @Test
    void testAfterControllerMethodCall2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.afterControllerMethodCall(joinPoint));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomAuthenticationException)}
     */
    @Test
    void testLogUserAccessDeniedException() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new CustomAuthenticationException("Msg"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomAuthenticationException)}
     */
    @Test
    void testLogUserAccessDeniedException2() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class,
                () -> aspectLogging.logUserAccessDeniedException(joinPoint, new CustomAuthenticationException("Msg")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomDynamoDbException)}
     */
    @Test
    void testLogUserAccessDeniedException3() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new CustomDynamoDbException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomDynamoDbException)}
     */
    @Test
    void testLogUserAccessDeniedException4() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class,
                () -> aspectLogging.logUserAccessDeniedException(joinPoint, new CustomDynamoDbException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomerAlreadyExistsException)}
     */
    @Test
    void testLogUserAccessDeniedException5() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new CustomerAlreadyExistsException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, CustomerAlreadyExistsException)}
     */
    @Test
    void testLogUserAccessDeniedException6() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logUserAccessDeniedException(joinPoint,
                new CustomerAlreadyExistsException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, InvalidRequestBodyException)}
     */
    @Test
    void testLogUserAccessDeniedException7() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new InvalidRequestBodyException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, InvalidRequestBodyException)}
     */
    @Test
    void testLogUserAccessDeniedException8() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logUserAccessDeniedException(joinPoint,
                new InvalidRequestBodyException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, RegistrationFailedException)}
     */
    @Test
    void testLogUserAccessDeniedException9() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new RegistrationFailedException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, RegistrationFailedException)}
     */
    @Test
    void testLogUserAccessDeniedException10() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> aspectLogging.logUserAccessDeniedException(joinPoint,
                new RegistrationFailedException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, UserNotFoundException)}
     */
    @Test
    void testLogUserAccessDeniedException11() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new UserNotFoundException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, UserNotFoundException)}
     */
    @Test
    void testLogUserAccessDeniedException12() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class,
                () -> aspectLogging.logUserAccessDeniedException(joinPoint, new UserNotFoundException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, UserServiceException)}
     */
    @Test
    void testLogUserAccessDeniedException13() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("Short String");
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act
        aspectLogging.logUserAccessDeniedException(joinPoint, new UserServiceException("An error occurred"));

        // Assert that nothing has changed
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Method under test:
     * {@link AspectLogging#logUserAccessDeniedException(JoinPoint, UserServiceException)}
     */
    @Test
    void testLogUserAccessDeniedException14() {
        // Arrange
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenThrow(new CustomDynamoDbException("An error occurred"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class,
                () -> aspectLogging.logUserAccessDeniedException(joinPoint, new UserServiceException("An error occurred")));
        verify(joinPoint).getSignature();
        verify(signature).toShortString();
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link AspectLogging#controllerAndServiceImplMethods()}
     *   <li>{@link AspectLogging#deleteMappingMethods()}
     *   <li>{@link AspectLogging#getMappingMethods()}
     *   <li>{@link AspectLogging#postMappingMethods()}
     *   <li>{@link AspectLogging#putMappingMethods()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     AspectLogging.logger

        // Arrange
        AspectLogging aspectLogging = new AspectLogging();

        // Act
        aspectLogging.controllerAndServiceImplMethods();
        aspectLogging.deleteMappingMethods();
        aspectLogging.getMappingMethods();
        aspectLogging.postMappingMethods();
        aspectLogging.putMappingMethods();
    }
}

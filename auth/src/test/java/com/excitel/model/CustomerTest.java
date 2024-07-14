package com.excitel.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    @Test
    public void testGettersAndSetters() {
        // Given
        String mobileNumber = "1234567890";
        String password = "password123";
        String name = "John Doe";
        String address = "123 Main St";
        String pinCode = "12345";
        String state = "CA";
        String country = "USA";
        String simType = "4G";
        String email = "johndoe@example.com";
        String role = "USER";

        // When
        Customer customer = new Customer();
        customer.setMobileNumber(mobileNumber);
        customer.setPassword(password);
        customer.setName(name);
        customer.setAddress(address);
        customer.setPinCode(pinCode);
        customer.setState(state);
        customer.setCountry(country);
        customer.setSimType(simType);
        customer.setEmail(email);
        customer.setRole(role);

        // Then
        assertEquals(mobileNumber, customer.getMobileNumber());
        assertEquals(password, customer.getPassword());
        assertEquals(name, customer.getName());
        assertEquals(address, customer.getAddress());
        assertEquals(pinCode, customer.getPinCode());
        assertEquals(state, customer.getState());
        assertEquals(country, customer.getCountry());
        assertEquals(simType, customer.getSimType());
        assertEquals(email, customer.getEmail());
        assertEquals(role, customer.getRole());
    }

    @Test
    public void testUserDetailsMethods() {
        // Given
        Customer customer = new Customer();
        String mobileNumber = "1234567890";
        customer.setMobileNumber(mobileNumber);

        // When/Then
        assertEquals(mobileNumber, customer.getUsername());
        assertTrue(customer.isAccountNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isEnabled());
    }

//    @Test
//    public void testDynamoDBAnnotations() throws NoSuchFieldException {
//        // Given
//        DynamoDBTable tableAnnotation = Customer.class.getAnnotation(DynamoDBTable.class);
//
//        // Then
//        assertNotNull(tableAnnotation);
//        assertEquals("customer-table", tableAnnotation.tableName());
//
//        // Check attribute annotations
//        assertAnnotationPresent(Customer.class, "mobileNumber", DynamoDBHashKey.class);
//        assertAnnotationPresent(Customer.class, "password", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "name", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "address", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "pinCode", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "state", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "country", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "simType", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "email", DynamoDBAttribute.class);
//        assertAnnotationPresent(Customer.class, "role", DynamoDBAttribute.class);
//    }

    @Test
    public void testEmptyConstructor() {
        // Given/When
        Customer customer = new Customer();

        // Then
        assertNull(customer.getMobileNumber());
        assertNull(customer.getPassword());
        assertNull(customer.getName());
        assertNull(customer.getAddress());
        assertNull(customer.getPinCode());
        assertNull(customer.getState());
        assertNull(customer.getCountry());
        assertNull(customer.getSimType());
        assertNull(customer.getEmail());
        assertEquals("USER", customer.getRole()); // Default role should be "USER"
    }

//    @Test
//    public void testEqualsAndHashCode() {
//        // Given
//        Customer customer1 = new Customer("1234567890", "password123", "John Doe", "123 Main St", "12345", "CA", "USA", "4G", "johndoe@example.com", "USER");
//        Customer customer2 = new Customer("1234567890", "password123", "John Doe", "123 Main St", "12345", "CA", "USA", "4G", "johndoe@example.com", "USER");
//        Customer customer3 = new Customer("9876543210", "pass123", "Jane Smith", "456 Elm St", "54321", "NY", "USA", "5G", "janesmith@example.com", "ADMIN");
//
//        // Then
//        assertEquals(customer1, customer2);
//        assertNotEquals(customer1, customer3);
//        assertEquals(customer1.hashCode(), customer2.hashCode());
//        assertNotEquals(customer1.hashCode(), customer3.hashCode());
//    }

    //          private void assertAnnotationPresent(Class<?> clazz, String fieldName, Class<?> annotationClass) throws NoSuchFieldException {
//        assertNotNull(clazz.getDeclaredField(fieldName).getAnnotation(annotationClass));
//    }
    @Test
    public void testGetAuthorities() {
        // Given
        Customer customer = new Customer();

        // When
        Collection<? extends GrantedAuthority> authorities = customer.getAuthorities();

        // Then
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    public void testGetUsername() {
        // Given
        String mobileNumber = "1234567890";
        Customer customer = new Customer();
        customer.setMobileNumber(mobileNumber);

        // When
        String username = customer.getUsername();

        // Then
        assertEquals(mobileNumber, username);
    }

    @Test
    public void testIsAccountNonExpired() {
        // Given
        Customer customer = new Customer();

        // When
        boolean accountNonExpired = customer.isAccountNonExpired();

        // Then
        assertTrue(accountNonExpired);
    }

    @Test
    public void testIsAccountNonLocked() {
        // Given
        Customer customer = new Customer();

        // When
        boolean accountNonLocked = customer.isAccountNonLocked();

        // Then
        assertTrue(accountNonLocked);
    }

    @Test
    public void testIsCredentialsNonExpired() {
        // Given
        Customer customer = new Customer();

        // When
        boolean credentialsNonExpired = customer.isCredentialsNonExpired();

        // Then
        assertTrue(credentialsNonExpired);
    }

    @Test
    public void testIsEnabled() {
        // Given
        Customer customer = new Customer();

        // When
        boolean enabled = customer.isEnabled();

        // Then
        assertTrue(enabled);
    }
    @Test
    public void testAllArgsConstructor() {
        // Given
        String mobileNumber = "1234567890";
        String password = "password";
        String name = "John Doe";

        // When
        Customer customer = new Customer();
        customer.setMobileNumber(mobileNumber);
        customer.setPassword(password);
        customer.setName(name);

        // Then
        assertNotNull(customer);
        assertEquals(mobileNumber, customer.getMobileNumber());
        assertEquals(password, customer.getPassword());
        assertEquals(name, customer.getName());
    }
    @Test
    public void testToString() {
        // Given
        String mobileNumber = "1234567890";
        String name = "John Doe";
        String address = "Main street";

        Customer customer = new Customer();
        customer.setMobileNumber(mobileNumber);
        customer.setName(name);
        customer.setAddress(address);

        // When
        String toStringResult = customer.toString();

        // Then
        String expectedToString = "Customer(mobileNumber=1234567890, password=null, name=John Doe, address=Main street, pinCode=null, state=null, country=null, simType=null, email=null, role=USER)";
        assertEquals(expectedToString, toStringResult);
    }
    @Test
    public void testBuilder() {
        // Given
        String mobileNumber = "1234567890";
        String name = "John Doe";

        // When
        Customer customer = Customer.builder()
                .mobileNumber(mobileNumber)
                .name(name)
                .build();

        // Then
        assertEquals(mobileNumber, customer.getMobileNumber());
        assertEquals(name, customer.getName());
    }
}

package com.excitel.model;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@DynamoDBTable(tableName = "customer-table")
@JsonIgnoreProperties(value= {"username", "enabled", "authorities", "accountNonExpired", "credentialsNonExpired", "accountNonLocked" })
public class Customer implements UserDetails {

    @DynamoDBHashKey(attributeName = "MobileNumber")
    private String mobileNumber;

    @DynamoDBAttribute(attributeName = "Password")
    private String password;

    @DynamoDBAttribute(attributeName = "CustomerName")
    private String name;

    @DynamoDBAttribute(attributeName = "Address")
    private String address;

    @DynamoDBAttribute(attributeName = "PINCode")
    private String pinCode;

    @DynamoDBAttribute(attributeName = "CustomerState")
    private String state;

    @DynamoDBAttribute(attributeName = "Country")
    private String country;

    @DynamoDBAttribute(attributeName = "SIMType")
    private String simType;

    @DynamoDBAttribute(attributeName = "Email")
    private String email;

    @DynamoDBAttribute(attributeName = "Role")
    private String role = "USER";

    @DynamoDBIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @DynamoDBIgnore
    @Override
    public String getUsername() {
        return this.mobileNumber;
    }

    @DynamoDBIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @DynamoDBIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @DynamoDBIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @DynamoDBIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

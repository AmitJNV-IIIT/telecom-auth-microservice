package com.excitel.constant;


public enum AppConstants {

    JWT_TOKEN_VALIDITY("" + (5L * 60 * 60)), // 5 hours
    CLIENT_ID_LENGTH(""+(7)),

    HASH_KEY_LENGTH  (""+11),
     MOBILE("MobileNumber"),
    EMAIL("Email"),
    ERROR("Error"),


     CLIENT_ID_CHARS ("0123456789"),
     HASH_KEY_CHARS  ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),


    TABLE("visitor-count-table"),


    FROM_NUMBER ( "+18183304150"),
    DETAILS ( "_details"),
    CUSTOMERS ("Customer"),
    INVALIDATE_TOKEN("__invalidatedTokens__"),
    PASSWORD("Password"),
    USER_ROLE("UserRole"),
    CREDENTIALS ("_cred");
    private final String value;

    AppConstants(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }

}

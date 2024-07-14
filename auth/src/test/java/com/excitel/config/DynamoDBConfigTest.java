package com.excitel.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DynamoDBConfig.class)
public class DynamoDBConfigTest {
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Test
    public void amazonDynamoDBBeanCreated() {
        assertNotNull(amazonDynamoDB);
    }

    @Test
    public void dynamoDbClientBeanCreated() {
        assertNotNull(dynamoDbClient);
    }
}

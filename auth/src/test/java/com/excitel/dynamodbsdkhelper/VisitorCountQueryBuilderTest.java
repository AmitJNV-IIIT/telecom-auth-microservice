package com.excitel.dynamodbsdkhelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class VisitorCountQueryBuilderTest {

    @InjectMocks
    private VisitorCountQueryBuilder visitorCountQueryBuilder;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @BeforeEach
    public void setup() {
        visitorCountQueryBuilder = new VisitorCountQueryBuilder();
    }

    @Test
    public void testQueryRequest() {
        QueryRequest queryRequest = visitorCountQueryBuilder.queryRequest(2021);

        assertEquals("visitor-count-table", queryRequest.tableName());
        assertEquals("#yr = :yearVal", queryRequest.keyConditionExpression());
        assertEquals(60, queryRequest.limit());
        assertEquals(false, queryRequest.scanIndexForward());

        Map<String, String> expressionAttributeNames = queryRequest.expressionAttributeNames();
        assertEquals("Year", expressionAttributeNames.get("#yr"));

        Map<String, AttributeValue> expressionAttributeValues = queryRequest.expressionAttributeValues();
        assertEquals("2021", expressionAttributeValues.get(":yearVal").n());
    }
}
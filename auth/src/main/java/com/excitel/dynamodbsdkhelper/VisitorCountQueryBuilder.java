package com.excitel.dynamodbsdkhelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.*;
import static com.excitel.constant.AppConstants.TABLE;


/**
 * This class helps build query requests for retrieving visitor count data from DynamoDB.
 * It provides a method to construct a query request based on a specific year.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@Service
public class VisitorCountQueryBuilder {

    @Autowired
    private DynamoDbClient dynamoDbClient;
    /**
     * Creates and returns a {@link QueryRequest} object for retrieving visitor count data
     * based on the specified year.
     *
     * @param year The year for which to retrieve visitor count data.
     * @return A {@link QueryRequest} object configured for the specified year.
     */
    public QueryRequest queryRequest(int year) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();

        // Use a placeholder for the reserved keyword
        expressionAttributeNames.put("#yr", "Year");

        // Bind the actual year value to the placeholder in the expression value
        expressionAttributeValues.put(":yearVal", AttributeValue.builder().n(String.valueOf(year)).build());

        return QueryRequest.builder()
                .tableName(TABLE.getValue())
                .keyConditionExpression("#yr = :yearVal")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .limit(60)
                .scanIndexForward(false)
                .build();
    }
}

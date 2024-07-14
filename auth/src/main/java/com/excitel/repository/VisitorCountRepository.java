package com.excitel.repository;
/**
 * This repository class provides methods for retrieving visitor count data from DynamoDB.
 * It leverages a {@link VisitorCountQueryBuilder} to construct DynamoDB queries.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */

import com.excitel.dynamodbsdkhelper.VisitorCountQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class VisitorCountRepository {

    @Autowired //NOSONAR
    private VisitorCountQueryBuilder queryBuilder;

    @Autowired //NOSONAR
    private DynamoDbClient dynamoDbClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM").withZone(ZoneId.systemDefault());
    /**
     * Retrieves visitor count data for the specified year.
     * This method uses the `queryBuilder` to construct a QueryRequest and retrieves visitor count data from DynamoDB.
     * It then processes the response by converting timestamps to formatted dates and returning a list of maps containing
     * formatted data (date and visitor count).
     *
     * @return A list of maps containing formatted visitor count data (date and visitor count).
     */
    public List<Map<String, String>> getVisitorCount(){
        QueryRequest queryRequest = queryBuilder.queryRequest(2024);
        QueryResponse response = dynamoDbClient.query(queryRequest);
        return formatResponse(response);
    }

    /**
     * Formats the raw DynamoDB query response data into a list of maps containing human-readable information.
     * This method iterates through each item in the response, converts the Unix timestamp to a formatted date string,
     * and creates a map with "data" (formatted date) and "value" (visitor count) keys. The list of formatted maps is then returned.
     *
     * @param response The DynamoDB query response containing raw data.
     * @return A list of maps containing formatted visitor count data (date and visitor count).
     */
    public List<Map<String, String>> formatResponse(QueryResponse response) {
        List<Map<String, String>> formattedData = new ArrayList<>();

        response.items().forEach(item -> {
            String unixStamp = item.get("UnixStamp").n();
            String visitorCount = item.get("VisitorCount").n();

            // Convert Unix timestamp to formatted date string
            long timestamp = Long.parseLong(unixStamp);
            String date = DATE_FORMATTER.format(Instant.ofEpochMilli(timestamp));

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("data", date);
            dataMap.put("value", visitorCount);

            formattedData.add(dataMap);
        });

        return formattedData;
    }
}

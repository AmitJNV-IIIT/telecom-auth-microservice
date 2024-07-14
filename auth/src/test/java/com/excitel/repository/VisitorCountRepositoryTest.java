package com.excitel.repository;

import com.excitel.dynamodbsdkhelper.VisitorCountQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitorCountRepositoryTest {

    @InjectMocks
    private VisitorCountRepository visitorCountRepository;

    @Mock
    private VisitorCountQueryBuilder queryBuilder;

    @Mock
    private DynamoDbClient dynamoDbClient;

    @BeforeEach
    public void setup() {
        // You may need to setup some common functionality here
    }

    @Test
    public void testGetVisitorCount() {
        // Given
        QueryRequest queryRequest = mock(QueryRequest.class);
        when(queryBuilder.queryRequest(2024)).thenReturn(queryRequest);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("UnixStamp", AttributeValue.builder().n("1621524400000").build()); // 20th May 2021
        item.put("VisitorCount", AttributeValue.builder().n("50").build());

        QueryResponse response = QueryResponse.builder()
                .items(item)
                .build();
        when(dynamoDbClient.query(queryRequest)).thenReturn(response);

        // When
        List<Map<String, String>> result = visitorCountRepository.getVisitorCount();

        // Then
        assertEquals(1, result.size());
        Map<String, String> resultMap = result.get(0);
        assertEquals("20 May", resultMap.get("data"));
        assertEquals("50", resultMap.get("value"));
    }

    @Test
    public void testGetVisitorCountWhenQueryFails() {
        // Given
        QueryRequest queryRequest = mock(QueryRequest.class);
        when(queryBuilder.queryRequest(2024)).thenReturn(queryRequest);
        when(dynamoDbClient.query(queryRequest)).thenThrow(RuntimeException.class);

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> visitorCountRepository.getVisitorCount());

        // Then
        assertNotNull(exception);
    }
}

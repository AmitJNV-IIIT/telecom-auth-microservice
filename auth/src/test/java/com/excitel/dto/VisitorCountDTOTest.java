package com.excitel.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VisitorCountDTOTest {

    private VisitorCountDto visitorCountDto;

    @BeforeEach
    public void setUp() {
        visitorCountDto = new VisitorCountDto();
    }

    @Test
    public void testSetStatus() {
        visitorCountDto.setStatus(HttpStatus.OK);
        assertEquals(HttpStatus.OK, visitorCountDto.getStatus());
    }

    @Test
    public void testSetData() {
        List<Map<String, String>> data = Collections.singletonList(Collections.singletonMap("key", "value"));
        visitorCountDto.setData(data);
        assertEquals(data, visitorCountDto.getData());
    }
}
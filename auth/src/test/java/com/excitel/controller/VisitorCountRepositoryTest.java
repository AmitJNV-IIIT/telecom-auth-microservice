package com.excitel.controller;

import com.excitel.dto.VisitorCountDto;
import com.excitel.serviceimpl.VisitorCountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class VisitorCountRepositoryTest {
    @InjectMocks
    private VisitorCountController visitorCountController;

    @Mock
    private VisitorCountServiceImpl visitorCountService;

    @Test
    public void testGetVisitorCount() {
        ResponseEntity<VisitorCountDto> responseEntity = visitorCountController.getVisitorCount();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}

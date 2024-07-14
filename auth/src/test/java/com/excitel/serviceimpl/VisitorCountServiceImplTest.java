package com.excitel.serviceimpl;

import com.excitel.repository.VisitorCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VisitorCountServiceImplTest {

    @InjectMocks
    private VisitorCountServiceImpl visitorCountService;

    @Mock
    private VisitorCountRepository visitorCountRepository;


    @Test
    public void testGetVisitorCount() {
        Map<String, String> visitorCountMap = new HashMap<>();
        visitorCountMap.put("date", "2021-08-10");
        visitorCountMap.put("count", "100");

        List<Map<String, String>> visitorCounts = Arrays.asList(visitorCountMap);

        when(visitorCountRepository.getVisitorCount()).thenReturn(visitorCounts);

        List<Map<String, String>> result = visitorCountService.getVisitorCount();

        assertEquals(visitorCounts, result);
    }
}
package com.excitel.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VisitorCountDto {
    private HttpStatus status; //NOSONAR
    private List<Map<String, String>> data; //NOSONAR
}

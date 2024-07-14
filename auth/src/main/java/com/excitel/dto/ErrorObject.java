package com.excitel.dto;
import lombok.Data;
import java.util.Date;


@Data
public class ErrorObject {
    private String status; //NOSONAR
    private String message; //NOSONAR
    private Date timestamp; //NOSONAR
}
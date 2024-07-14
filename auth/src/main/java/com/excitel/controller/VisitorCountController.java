package com.excitel.controller;

import com.excitel.dto.VisitorCountDto;
import com.excitel.serviceimpl.VisitorCountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * This controller class handles API requests related to visitor counts.
 * It exposes an endpoint to retrieve the current visitor count.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@RestController
@RequestMapping("/api/v2/auth/")
public class VisitorCountController {

    @Autowired
    private VisitorCountServiceImpl visitorCountService;

    /**
     * GET endpoint to retrieve the current visitor count.
     *
     * @return A {@link ResponseEntity} containing a {@link VisitorCountDto} object
     * with the visitor count data and HTTP status code (OK in this case).
     */
    @GetMapping("visitor-count")
    public ResponseEntity<VisitorCountDto> getVisitorCount(){
        return ResponseEntity.ok(VisitorCountDto.builder().data(visitorCountService.getVisitorCount()).status(HttpStatus.OK).build());
    }

}

package com.excitel.serviceimpl;

import com.excitel.repository.VisitorCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VisitorCountServiceImpl {
    @Autowired
    private VisitorCountRepository repository;

    public List<Map<String, String>> getVisitorCount(){
        return repository.getVisitorCount();
    }

}

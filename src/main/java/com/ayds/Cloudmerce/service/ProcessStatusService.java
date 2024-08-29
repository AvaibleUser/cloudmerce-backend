package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.repository.ProcessStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessStatusService {

    private final ProcessStatusRepository processStatusRepository;

    public ProcessStatusEntity existsProcessStatusByProcessId(Integer id){
        return processStatusRepository.findById(id).orElse(null);
    }

    public ProcessStatusEntity existsProcessStatusByProcessStatus(String status){
        return  processStatusRepository.findByStatus(status).orElse(null);
    }
}

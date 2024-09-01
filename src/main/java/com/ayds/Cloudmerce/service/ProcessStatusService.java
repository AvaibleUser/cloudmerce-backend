package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.cart.ProcessStatusDTO;
import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.model.entity.ProcessStatusEntity;
import com.ayds.Cloudmerce.repository.ProcessStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessStatusService {

    private final ProcessStatusRepository processStatusRepository;

    public ProcessStatusEntity existsProcessStatusByProcessId(Integer id){
        return processStatusRepository.findById(id).orElse(null);
    }

    public List<ProcessStatusEntity> getAllProcessStatusEntity(){
        return processStatusRepository.findAll();
    }

    public ProcessStatusEntity existsProcessStatusByProcessStatus(String status){
        return  processStatusRepository.findByStatus(status).orElse(null);
    }

    public List<ProcessStatusDTO> getAllProcessStatus(){
        return this.processStatusRepository.findAll().stream()
                .map(this::convertProcessStatusTDO)
                .collect(Collectors.toList());
    }

    private ProcessStatusDTO convertProcessStatusTDO(ProcessStatusEntity processStatusEntity){
        return new ProcessStatusDTO(processStatusEntity.getId(),processStatusEntity.getStatus());
    }

    public String processStatus(List<ProcessStatusEntity> lis, Integer id){
        for (ProcessStatusEntity processStatusEntity : lis) {
            if (processStatusEntity.getId().equals(id)) {
                return processStatusEntity.getStatus();
            }
        }
        return null;
    }
}

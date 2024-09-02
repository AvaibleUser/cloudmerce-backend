package com.ayds.Cloudmerce.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.dto.CompanyRegisterDto;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public CompanyEntity registerCompany(CompanyRegisterDto company) throws IOException {
        if (!new File(company.logoPath()).exists()) {
            throw new FileNotFoundException("The file does not exists");
        }
        return companyRepository.save(new CompanyEntity(company.name(), company.address(),
                company.shippingCost(), company.logoPath()));
    }

    public Optional<CompanyEntity> findCompany(long companyId) {
        return companyRepository.findById(companyId);
    }
}

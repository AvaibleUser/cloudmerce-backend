package com.ayds.Cloudmerce.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.Cloudmerce.model.dto.CompanyRegisterDto;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.service.CompanyService;
import com.ayds.Cloudmerce.service.FileStorageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FileStorageService storageService;

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyEntity> getCompanyById(@PathVariable @Positive long companyId) {
        Optional<CompanyEntity> company = companyService.findCompany(companyId);

        return ResponseEntity.of(company);
    }

    @PostMapping
    public ResponseEntity<CompanyEntity> createCompany(@RequestPart("logo-file") @NotEmpty MultipartFile logo,
            @RequestPart("details") @Valid CompanyRegisterDto company) throws IOException {
        String logoFileName = storageService.store(logo);
        CompanyEntity savedCompany = companyService.registerCompany(company.withLogoPath(logoFileName));
        return new ResponseEntity<>(savedCompany, HttpStatus.CREATED);
    }
}

package com.ayds.Cloudmerce.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.Cloudmerce.model.dto.CompanyRegisterDto;
import com.ayds.Cloudmerce.model.dto.CompanyUpdateDto;
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
            @RequestPart("details") @Valid CompanyRegisterDto company) {
        CompanyEntity savedCompany = companyService.registerCompany(company);
        String logoPath = storageService.store("company_" + savedCompany.getId(), logo);
        savedCompany = companyService.changeLogoPath(savedCompany.getId(), logoPath);

        return new ResponseEntity<>(savedCompany, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyEntity> updateCompany(@PathVariable @Positive long companyId,
            @RequestBody @Valid CompanyUpdateDto company) {
        CompanyEntity changedCompany = companyService.changeCompanyInfo(companyId, company);

        return ResponseEntity.ok(changedCompany);
    }

    @PutMapping("/{copmanyId}/image")
    public ResponseEntity<CompanyEntity> changeCompanyImage(@PathVariable @Positive long companyId,
            @RequestPart MultipartFile imageFile) {
        Optional<CompanyEntity> company = companyService.findCompany(companyId);

        company.ifPresent(comp -> storageService.store("company_" + comp.getId(), imageFile));

        return ResponseEntity.of(company);
    }
}

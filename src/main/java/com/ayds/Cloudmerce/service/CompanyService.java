package com.ayds.Cloudmerce.service;

import static java.util.function.Predicate.not;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ayds.Cloudmerce.model.dto.CompanyRegisterDto;
import com.ayds.Cloudmerce.model.dto.CompanyUpdateDto;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
import com.ayds.Cloudmerce.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Optional<CompanyEntity> findCompany(long companyId) {
        return companyRepository.findById(companyId);
    }

    public CompanyEntity registerCompany(CompanyRegisterDto company) {
        return companyRepository.save(new CompanyEntity(company.name(), company.address(), company.shippingCost()));
    }

    public CompanyEntity changeCompanyInfo(long companyId, CompanyUpdateDto company) {
        CompanyEntity dbCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new ValueNotFoundException("La compañia no se encuentra registrada"));

        company.address()
                .filter(not(ObjectUtils::isEmpty))
                .ifPresent(dbCompany::setAddress);

        company.name()
                .filter(not(ObjectUtils::isEmpty))
                .ifPresent(dbCompany::setName);

        company.shippingCost()
                .ifPresent(dbCompany::setShippingCost);

        return companyRepository.save(dbCompany);
    }

    public CompanyEntity changeLogoPath(long companyId, String logoPath) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ValueNotFoundException("La compañia no se encuentra registrada"));

        company.setLogoPath(logoPath);

        return companyRepository.save(company);
    }
}

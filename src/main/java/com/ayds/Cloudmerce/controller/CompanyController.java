package com.ayds.Cloudmerce.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.ayds.Cloudmerce.model.dto.CompanyRegisterDto;
import com.ayds.Cloudmerce.model.entity.CompanyEntity;
import com.ayds.Cloudmerce.repository.CompanyRepository;
import com.ayds.Cloudmerce.service.CompanyService;
import com.ayds.Cloudmerce.service.FileStorageService;
import com.ayds.Cloudmerce.service.PdfGeneratorService;

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

    // TODO: Pasar a contoller correcto
    @Autowired
    private CompanyRepository companyRepository;

    // TODO: Pasar a contoller correcto
    @Autowired
    private JpaRepository<?, Long> orderRepository;

    // TODO: Pasar a contoller correcto
    @Autowired
    private PdfGeneratorService pdfService;

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

    // TODO: Pasar a contoller correcto
    @PutMapping("/carrito")
    public ResponseEntity<?> buy(@PathVariable long orderId, @RequestBody Object carrito) {
        Context context = new Context();
        Object order = orderRepository.findById(orderId).get();
        Object cart = order.getClass(); // order.getCart()

        context.setVariable("bill", order);
        context.setVariable("cart", cart);
        context.setVariable("client", cart.getClass()); // cart.getClient()
        context.setVariable("cartItems", cart.getClass()); // cart.getCartItems();
        context.setVariable("company", companyRepository.findAll()
                .stream()
                .findFirst()
                .get());

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        String billHtml = templateEngine.process("bill", context);

        byte[] pdfBytes;
        try {
            pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);
            try (InputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
                storageService.store("factura_" + orderId + ".pdf", inputStream, MediaType.APPLICATION_PDF_VALUE, pdfBytes.length);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseEntity.ok(null);
    }

}

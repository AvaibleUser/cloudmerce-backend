package com.ayds.Cloudmerce.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.entity.OrderEntity;
import com.ayds.Cloudmerce.service.PdfGeneratorService;
import com.ayds.Cloudmerce.service.TemplateRendererService;

@RestController
@RequestMapping("/api/prueba-pdf")
public class PruebaController {

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private PdfGeneratorService pdfService;

    @GetMapping
    public ResponseEntity<Resource> downloadTest(OrderEntity order) {
        String billHtml = templateRendererService.renderTemplate("prueba", null);

        try {
            byte[] pdfBytes = pdfService.generatePdfFromHtmlString(billHtml);

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("pdf-test.pdf")
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.badRequest().build();
        }
    }
}

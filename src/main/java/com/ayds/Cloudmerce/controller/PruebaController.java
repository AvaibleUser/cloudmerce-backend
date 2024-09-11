package com.ayds.Cloudmerce.controller;

import java.io.IOException;

import com.ayds.Cloudmerce.service.report.DownloadExcelService;
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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/prueba")
public class PruebaController {

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private PdfGeneratorService pdfService;

    @Autowired
    private DownloadExcelService downloadExcelService;

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

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        // Crear un libro de Excel (Workbook)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Ventas");

        // Crear estilo para el encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);  // Establecer negrita
        headerStyle.setFont(headerFont);

        // Crear encabezado de la tabla
        String[] headers = { "Producto", "Cantidad Vendida", "Precio Unitario", "Total" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Añadir datos (aquí puedes agregar los datos que necesites)
        Object[][] salesData = {
                {"Producto A", 50, 10.0, 500.0},
                {"Producto B", 30, 20.0, 600.0},
                {"Producto C", 15, 30.0, 450.0},
        };

        int rowNum = 1;
        for (Object[] sale : salesData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < sale.length; i++) {
                row.createCell(i).setCellValue(sale[i].toString());
            }
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir los datos a un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Preparar la respuesta HTTP con el archivo Excel
        HttpHeaders headersHttp = new HttpHeaders();
        headersHttp.setContentDispositionFormData("attachment", "ReporteVentas.xlsx");
        headersHttp.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headersHttp)
                .body(outputStream.toByteArray());
    }

    @GetMapping("/Download-excel")
    public ResponseEntity<byte[]> DownloadToExcel() throws IOException{
        List<String> headers = new ArrayList<>();
        List<Object> salesData = new ArrayList<>();
        salesData.add("Producto A");
        salesData.add(50.00);
        salesData.add(10.00);
        salesData.add(500.00);
        salesData.add("Producto B");
        salesData.add(25.00);
        salesData.add(12.00);
        salesData.add(400.00);
        salesData.add("Producto C");
        salesData.add(70.00);
        salesData.add(80.00);
        salesData.add(650.00);
        headers.add("Producto");
        headers.add("Cantidad Vendida");
        headers.add("Precio Unitario");
        headers.add("Total");
        return this.downloadExcelService.generateExcelReport(headers, salesData, "reportePrueba");
    }

}

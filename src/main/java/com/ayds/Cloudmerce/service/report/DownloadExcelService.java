package com.ayds.Cloudmerce.service.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DownloadExcelService {

    public ResponseEntity<byte[]> generateExcelReport(List<String> headers, List<Object> salesData, String nameReport) throws IOException {
        // Crear un libro de Excel (Workbook)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(nameReport);

        // Crear estilo para el encabezado
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);  // Establecer negrita
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        int colum = 0;
        Row row = sheet.createRow(rowNum++);
        for (Object sale : salesData) {
            if (colum == headers.size()) {
                row = sheet.createRow(rowNum++);
                colum = 0;
            }
            row.createCell(colum).setCellValue(sale.toString());
            colum++;
        }

        // Ajustar el tamaño de las columnas
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return downloadExcelReport(workbook, nameReport);

    }

    private ResponseEntity<byte[]> downloadExcelReport(Workbook workbook, String nameReport) throws IOException{
        // Escribir los datos a un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Preparar la respuesta HTTP con el archivo Excel
        HttpHeaders headersHttp = new HttpHeaders();
        headersHttp.setContentDispositionFormData("attachment", nameReport+".xlsx");
        headersHttp.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headersHttp)
                .body(outputStream.toByteArray());
    }
}

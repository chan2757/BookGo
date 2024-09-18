package com.bookgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    private final ObjectMapper objectMapper;

    public ExcelExportService() {
        this.objectMapper = new ObjectMapper();
    }

    // JSON 데이터를 엑셀로 출력하는 메서드
    public void exportJsonToExcel(List<Map<String, Object>> jsonData, HttpServletResponse response) throws IOException {
        if (jsonData.isEmpty()) {
            throw new IllegalArgumentException("JSON 데이터가 비어 있습니다.");
        }

        // 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exported Data");

        // 첫 번째 데이터에서 파일 이름과 헤더 생성
        String fileName = URLEncoder.encode("JSON_Data_Report.xlsx", "UTF-8");
        createExcelHeader(jsonData.get(0), sheet, workbook);  // 첫 번째 항목으로 헤더 생성
        populateExcelRows(jsonData, sheet);  // 데이터 작성

        // 응답 설정 및 파일 출력
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // 엑셀 헤더 생성
    private void createExcelHeader(Map<String, Object> data, Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        int colIdx = 0;

        for (String key : data.keySet()) {
            Cell cell = headerRow.createCell(colIdx++);
            cell.setCellValue(key);
            cell.setCellStyle(createHeaderCellStyle(workbook));
        }
    }

    // 엑셀 데이터 작성
    private void populateExcelRows(List<Map<String, Object>> jsonData, Sheet sheet) {
        int rowIdx = 1;

        for (Map<String, Object> data : jsonData) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;

            for (Object value : data.values()) {
                Cell cell = row.createCell(colIdx++);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }
    }

    // 헤더 셀 스타일 생성
    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
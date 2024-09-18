package com.bookgo.controller;

import com.bookgo.service.ExcelExportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/excel")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // JSON 데이터를 엑셀로 변환하여 다운로드하는 메서드
    @PostMapping("/exportJson")
    public void exportJsonToExcel(@RequestBody String jsonData, HttpServletResponse response) throws IOException {

        // JSON 데이터를 List<Map<String, Object>>로 변환
        List<Map<String, Object>> dataList = objectMapper.readValue(jsonData, new TypeReference<List<Map<String, Object>>>() {});

        // 엑셀 파일로 변환하여 응답으로 출력
        excelExportService.exportJsonToExcel(dataList, response);
    }
}


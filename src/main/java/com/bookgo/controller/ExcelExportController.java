package com.bookgo.controller;

import com.bookgo.service.ExcelExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/export")
    public void exportToExcel(@RequestParam("data") String jsonData,
                              @RequestParam("className") String className,
                              HttpServletResponse response) throws IOException {
        try {
            // 클래스 이름을 통해 VO 클래스 로드
            Class<?> voClass = Class.forName(className);
            // JSON 데이터를 VO 리스트로 변환
            List<?> voList = objectMapper.readValue(jsonData, objectMapper.getTypeFactory().constructCollectionType(List.class, voClass));

            // 엑셀 출력 서비스 호출
            excelExportService.exportVoListToExcel(voList, response);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid class name provided: " + className, e);
        }

    }

    @PostMapping("/exportVoList")
    public void exportVoListToExcel(@RequestParam("voList") List<?> voList, HttpServletResponse response) throws IOException {
        // VO 리스트를 엑셀로 출력하는 메서드 호출
        excelExportService.exportVoListToExcel(voList, response);
    }
}

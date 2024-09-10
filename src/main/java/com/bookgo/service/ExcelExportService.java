package com.bookgo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
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

    // 엑셀로 출력하는 메서드
    public void exportVoListToExcel(List<?> voList, HttpServletResponse response) throws IOException {
        // VO 리스트를 JSON으로 변환하여 평탄화
        List<Map<String, Object>> jsonData = flattenVoList(voList);

        if (jsonData.isEmpty()) {
            throw new IllegalArgumentException("VO 리스트가 비어 있습니다.");
        }

        // 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Exported Data");

        // 첫 번째 VO 객체에서 파일 이름과 헤더 생성
        String fileName = URLEncoder.encode(voList.get(0).getClass().getSimpleName() + "_Report.xlsx", "UTF-8");
        createExcelHeader(jsonData.get(0), sheet, workbook);
        populateExcelRows(jsonData, sheet);

        // 응답 설정 및 파일 출력
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // VO 리스트를 평탄화하여 JSON 데이터로 변환
    private List<Map<String, Object>> flattenVoList(List<?> voList) {
        List<Map<String, Object>> flatDataList = new ArrayList<>();

        for (Object vo : voList) {
            Map<String, Object> voMap = new HashMap<>();
            extractFields(vo, voMap);
            flatDataList.add(voMap);
        }

        return flatDataList;
    }

    // VO의 필드를 추출하여 Map에 추가
    private void extractFields(Object vo, Map<String, Object> voMap) {
        Field[] fields = vo.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(vo);

                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    if (!list.isEmpty() && isVoObject(list.get(0))) {
                        List<String> flattenedList = new ArrayList<>();
                        for (Object item : list) {
                            flattenedList.add(objectMapper.writeValueAsString(item));
                        }
                        voMap.put(field.getName(), String.join("; ", flattenedList));
                    } else {
                        voMap.put(field.getName(), value.toString());
                    }
                } else if (isVoObject(value)) {
                    voMap.put(field.getName(), objectMapper.writeValueAsString(value));
                } else {
                    voMap.put(field.getName(), value != null ? value.toString() : "");
                }

            } catch (IllegalAccessException | IOException e) {
                voMap.put(field.getName(), "Error extracting value");
            }
        }
    }

    // VO 객체인지 확인
    private boolean isVoObject(Object obj) {
        return obj != null && !(obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Enum);
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

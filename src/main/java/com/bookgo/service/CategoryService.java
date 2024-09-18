package com.bookgo.service;

import com.bookgo.mapper.CategoryMapper;
import com.bookgo.vo.BookCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Map<String, String> categoryMap = new HashMap<>();

    @EventListener(ContextRefreshedEvent.class)
    public void loadCategories() {
        List<BookCategoryVO> categories = categoryMapper.findAllCategories();
        for (BookCategoryVO category : categories) {
            categoryMap.put(category.getCategoryName(), category.getCategoryId());
        }
    }

    public String getCategoryNumber(String inputString) {
        // 입력 값이 null인지 확인하여 안전하게 처리
        if (inputString == null || inputString.trim().isEmpty()) {
            return "000"; // 기본값 반환
        }

        // 문자열을 " > "로 파싱하여 카테고리 이름 추출
        String[] parts = inputString.split(">");

        if (parts.length > 1) {
            String extractedCategoryName = parts[1].trim(); // 두 번째 항목을 카테고리 이름으로 사용

            // 카테고리 맵에서 이름을 포함하는 경우를 찾음
            for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
                String categoryName = entry.getKey();
                if (extractedCategoryName.contains(categoryName)) {
                    return entry.getValue(); // 포함하는 카테고리 ID 반환
                }
            }
        }

        // 카테고리 이름을 추출할 수 없으면 기본값 반환
        return "000";
    }

    public List<BookCategoryVO> getAllCategories() {
        List<BookCategoryVO> BCL = categoryMapper.findAllCategories();
        System.out.println("서비스: " + BCL);
        return BCL;
    }
}
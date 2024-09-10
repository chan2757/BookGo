package com.bookgo.controller;

import com.bookgo.mapper.FAQMapper;
import com.bookgo.vo.FAQBoardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FAQRestController {

    @Autowired
    private FAQMapper faqMapper;

    // 카테고리 목록을 가져오는 메서드
    @GetMapping("/voc/getCategoryList")
    public List<Map<String, Object>> getCategoryList() {
        // FAQ_CATEGORY 테이블에서 카테고리 목록을 가져옴
        return faqMapper.getCategoryList();
    }

    // 선택한 CATEGORYID에 해당하는 FAQ 데이터를 가져오는 메서드
    @GetMapping("/voc/getFAQByCategoryId")
    public List<FAQBoardVO> getFAQByCategoryId(@RequestParam("categoryId") int categoryId) {
        // 선택된 카테고리 ID에 해당하는 FAQ를 가져옴
        return faqMapper.getFAQByCategoryId(categoryId);
    }
}

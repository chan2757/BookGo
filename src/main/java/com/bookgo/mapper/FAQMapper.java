package com.bookgo.mapper;

import com.bookgo.vo.FAQBoardVO;

import java.util.List;
import java.util.Map;

public interface FAQMapper {

    // FAQ_CATEGORY 테이블에서 CATEGORYNAME과 CATEGORYID 조회
    List<Map<String, Object>> getCategoryList();

    // 선택한 CATEGORYID에 따라 FAQBOARD 테이블의 질문과 답변 조회
    List<FAQBoardVO> getFAQByCategoryId(int categoryId);
}

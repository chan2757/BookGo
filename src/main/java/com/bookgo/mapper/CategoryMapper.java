package com.bookgo.mapper;

import com.bookgo.vo.BookCategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 모든 카테고리 목록을 조회하는 메서드
     *
     * @return 모든 카테고리의 리스트
     */
    List<BookCategoryVO> findAllCategories();


}

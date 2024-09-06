package com.bookgo.mapper;

import com.bookgo.vo.BookDetailVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookDetailMapper {

    // XML 파일의 id와 메서드명이 일치해야 함
    BookDetailVO selectBookDetailByIsbn13(String isbn13);

    void insertBookDetail(BookDetailVO bookDetailVO);
}

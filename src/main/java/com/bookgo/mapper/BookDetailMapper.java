package com.bookgo.mapper;

import com.bookgo.vo.BookDetailVO;
import com.bookgo.vo.BookDpVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookDetailMapper {

    // XML 파일의 id와 메서드명이 일치해야 함
    BookDetailVO selectBookDetailByIsbn13(String isbn13);

    void insertBookDetail(BookDetailVO bookDetailVO);

    List<BookDpVO> selectBookDpByCategoryId(String categoryId);
}

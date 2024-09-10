package com.bookgo.mapper;

import com.bookgo.vo.BookProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookProductMapper {

    // 모든 책 정보를 조회하는 메서드
    List<BookProductVO> getAllProducts();

    // ISBN13을 기준으로 책의 재고를 수정하는 메서드
    void updateProductStock(@Param("isbn13") String isbn13, @Param("stock") int stock);
}

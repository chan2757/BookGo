package com.bookgo.service;

import com.bookgo.mapper.BookProductMapper;
import com.bookgo.vo.BookProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookProductService {

    @Autowired
    private BookProductMapper bookProductMapper;

    // 모든 책 정보를 가져오는 메서드
    public List<BookProductVO> getAllProducts() {
        return bookProductMapper.getAllProducts();
    }

    // 책의 재고를 업데이트하는 메서드
    public void updateProductStock(String isbn13, int stock) {
        bookProductMapper.updateProductStock(isbn13, stock);
    }

    public void updateStock(String isbn13, int stock) {
    }
}

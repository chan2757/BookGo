package com.bookgo.vo;


import lombok.Data;

@Data
public class CategoryVO {
    private Long categoryId; // 카테고리번호
    private String categoryName; // 카테고리이름
    private String description; // 카테고리설명
}

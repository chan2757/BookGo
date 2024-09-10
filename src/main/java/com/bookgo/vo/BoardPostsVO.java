package com.bookgo.vo;

import lombok.Data;

import java.sql.Date;

@Data
public class BoardPostsVO {
    private int postId; // 게시글번호
    private Long categoryId; // 카테고리번호
    private Long  userId; // ID (site_user의 ID)
    private String username; // ID (site_user의 ID)
    private String title; // 제목
    private String content; // 내용
    private String filename; // 파일명
    private Integer viewCount; // 조회수
    private Integer recommendCount; // 추천수
    private Date createdDate; // 작성일
    private String categoryName; // 카테고리이름
}

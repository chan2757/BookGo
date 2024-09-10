package com.bookgo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QnAVO {
    private Long qid;
    private String title;
    private String writer;
    private String content;
    private String filename;
    private LocalDateTime writeDate;
    private int readCount;
    private String status; // 답변 상태

    // Getters and Setters
}

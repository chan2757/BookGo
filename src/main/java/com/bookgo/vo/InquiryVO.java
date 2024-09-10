package com.bookgo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InquiryVO {
    private int inquiryId;
    private int userId;
    private String username;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    private int adminId  = 0; // 답변을 작성한 관리자 ID
    private String replyContent  = ""; // 답변 내용
    private LocalDateTime replyDate; // 답변 작성일

    private String status; // 답변 상태는 컨트롤러에서 동적으로 설정
}

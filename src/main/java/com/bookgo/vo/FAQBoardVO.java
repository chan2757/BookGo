package com.bookgo.vo;

import java.sql.Date;
import lombok.Data;

@Data
public class FAQBoardVO {
    private int faqId;
    private String question;
    private String answer;
    private Date createdDate;

    // Getters and Setters
}

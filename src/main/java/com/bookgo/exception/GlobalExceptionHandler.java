package com.bookgo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNullPointerException(NullPointerException ex, Model model) {
        logger.error("NullPointerException occurred: ", ex);
        model.addAttribute("errorMessage", "검색 결과가 없습니다.");
        return "error/noResults"; // 검색 결과 없음 페이지로 이동
    }
}

package com.bookgo.controller;

import com.bookgo.service.CommentService;
import com.bookgo.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 추천
    @PostMapping("/recommend/{commentId}")
    public ResponseEntity<Void> recommendComment(@PathVariable("commentId") Long commentId) {
        try {
            commentService.incrementRecommendCount(commentId);
            logger.debug("Recommended comment with ID: {}", commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error recommending comment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 삭제
    @PostMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CommentVO comment = commentService.getCommentById(commentId);
            if (comment != null && comment.getUsername().equals(userDetails.getUsername())) {
                commentService.deleteComment(commentId);
                logger.debug("Deleted comment with ID: {}", commentId);
                return ResponseEntity.ok().build();
            } else {
                logger.warn("Attempt to delete unauthorized comment with ID: {}", commentId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            logger.error("Error deleting comment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 수정
    @PostMapping("/edit/{commentId}")
    public void editComment(@PathVariable("commentId") Long commentId,
                            @RequestBody CommentVO commentVO,
                            @AuthenticationPrincipal UserDetails userDetails) {
        CommentVO existingComment = commentService.getCommentById(commentId);
        if (existingComment != null && existingComment.getUsername().equals(userDetails.getUsername())) {
            existingComment.setContent(commentVO.getContent());
            commentService.updateComment(existingComment);
            logger.debug("Edited comment: {}", existingComment);
        } else {
            logger.warn("Attempt to edit unauthorized comment with ID: {}", commentId);
        }
    }
}



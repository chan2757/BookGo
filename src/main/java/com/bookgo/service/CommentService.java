package com.bookgo.service;

import com.bookgo.mapper.CommentMapper;
import com.bookgo.vo.CommentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public List<CommentVO> getCommentsByPostId(Long postId) {
        return commentMapper.selectCommentsByPostId(postId);
    }

    public void addComment(CommentVO comment) {
        commentMapper.insertComment(comment);
    }

    @Transactional
    public void incrementRecommendCount(Long commentId) {
        CommentVO comment = commentMapper.getCommentById(commentId);
        if (comment != null) {
            comment.setRecommendCount(comment.getRecommendCount() + 1);
            commentMapper.updateComment(comment);
        }
    }

    // 댓글 ID로 댓글 조회
    public CommentVO getCommentById(Long commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentMapper.deleteComment(commentId);
    }

    @Transactional
    public void updateComment(CommentVO existingComment) {
        CommentVO comment = commentMapper.getCommentById(existingComment.getCommentId());
        if (comment != null) {
            comment.setContent(existingComment.getContent());
            commentMapper.updateComment(comment);
        }
    }


}

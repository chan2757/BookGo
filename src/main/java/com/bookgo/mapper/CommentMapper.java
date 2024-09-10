package com.bookgo.mapper;

import com.bookgo.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 삽입
    void insertComment(CommentVO commentVO);

    List<CommentVO> selectCommentsByPostId(Long postId);

    CommentVO getCommentById(Long commentId);

    void updateComment(CommentVO comment);

    void deleteComment(Long commentId);
}

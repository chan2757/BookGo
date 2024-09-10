package com.bookgo.service;

import com.bookgo.vo.BoardPostsVO;
import com.bookgo.mapper.BoardPostsMapper;
import com.bookgo.vo.CategoryVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardPostsService {

    private static final Logger logger = LoggerFactory.getLogger(BoardPostsService.class);

    private final BoardPostsMapper boardPostsMapper;

    @Autowired
    public BoardPostsService(BoardPostsMapper boardPostsMapper) {
        this.boardPostsMapper = boardPostsMapper;
    }

    public List<BoardPostsVO> getAllPosts() {
        logger.debug("Getting all posts");
        List<BoardPostsVO> posts = boardPostsMapper.selectAllPosts();
        logger.debug("Retrieved {} posts", posts.size());
        return posts;
    }


    public List<BoardPostsVO> getPostsByUsername(String username) {
        logger.debug("Searching posts by username: {}", username);
        List<BoardPostsVO> posts = boardPostsMapper.selectPostsByUsername(username);
        logger.debug("Retrieved {} posts for username: {}", posts.size(), username);
        return posts;
    }

    public List<BoardPostsVO> getPostsByContent(String content) {
        logger.debug("Searching posts by content: {}", content);
        List<BoardPostsVO> posts = boardPostsMapper.selectPostsByContent(content);
        logger.debug("Retrieved {} posts for content: {}", posts.size(), content);
        return posts;
    }

    public List<BoardPostsVO> getPostsByTitle(String title) {
        logger.debug("Searching posts by title: {}", title);
        List<BoardPostsVO> posts = boardPostsMapper.selectPostsByTitle(title);
        logger.debug("Retrieved {} posts for title: {}", posts.size(), title);
        return posts;
    }

    public BoardPostsVO getPostDetail(int postId) {
        return boardPostsMapper.selectPostById(postId);
    }


    public List<CategoryVO> getAllCategories() {
        logger.debug("Fetching all categories");
        return boardPostsMapper.selectAllCategories(); // 매퍼에서 카테고리 리스트를 가져옴
    }

    public List<BoardPostsVO> getPostsByCategory(Long categoryId) {
        logger.debug("Fetching posts for category ID: {}", categoryId);
        return boardPostsMapper.selectPostsByCategory(categoryId); // 선택한 카테고리의 게시글을 가져옴
    }

    // 게시글 상세 조회 시 조회수 증가
    public BoardPostsVO getPostById(int postId) {
        boardPostsMapper.incrementViewCount(postId); // 조회수 증가
        return boardPostsMapper.selectPostById(postId);
    }

    // 추천수 증가
    public void incrementRecommendCount(int postId) {
        boardPostsMapper.incrementRecommendCount(postId);
    }

    @Transactional
    public void insertPost(BoardPostsVO boardPost) {
        logger.debug("insertPost 메소드 호출됨");

        try {
            // 게시글 정보 로그 출력
            logger.debug("게시글 정보: categoryId={}, type={}, userId={}, type={}, username={}, type={}, title={}, type={}, content={}, type={}, filename={}, type={}",
                    boardPost.getCategoryId(), boardPost.getCategoryId().getClass().getName(),
                    boardPost.getUserId(), boardPost.getUserId().getClass().getName(),
                    boardPost.getUsername(), boardPost.getUsername().getClass().getName(),
                    boardPost.getTitle(), boardPost.getTitle().getClass().getName(),
                    boardPost.getContent(), boardPost.getContent().getClass().getName(),
                    boardPost.getFilename(), boardPost.getFilename() != null ? boardPost.getFilename().getClass().getName() : "null");

            // 게시글을 데이터베이스에 삽입
            boardPostsMapper.insertPost(boardPost);

            logger.debug("게시글이 성공적으로 데이터베이스에 삽입되었습니다.");
        } catch (Exception e) {
            logger.error("게시글 삽입 중 오류 발생", e);
            throw e; // 예외를 다시 던져서 트랜잭션 롤백을 유도합니다.
        }
    }

    public boolean deletePost(int postId) {
        return boardPostsMapper.deletePost(postId) > 0;
    }

    public boolean updatePost(int postId, String title, String content) {
        return boardPostsMapper.updatePost(postId, title, content) > 0;
    }

    // BoardPostsService.java
    public List<BoardPostsVO> getPostsByUserId(Long userId) {
        logger.debug("Fetching posts for user ID: {}", userId);
        return boardPostsMapper.selectPostsByUserId(userId);
    }


}

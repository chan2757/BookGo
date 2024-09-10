package com.bookgo.mapper;

import com.bookgo.vo.BoardPostsVO;
import com.bookgo.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BoardPostsMapper {

    // 전체 게시글 조회 (기본)
    List<BoardPostsVO> selectAllPosts();

    // 카테고리별 게시글 조회
    List<BoardPostsVO> selectPostsByCategory(Integer categoryId);

    // USERNAME으로 검색
    List<BoardPostsVO> selectPostsByUsername(String username);

    // 내용으로 검색
    List<BoardPostsVO> selectPostsByContent(String content);

    // 제목으로 검색
    List<BoardPostsVO> selectPostsByTitle(String title);

    BoardPostsVO selectPostById(int postId);

    List<CategoryVO> selectAllCategories();

    void incrementRecommendCount(int postId);

    void incrementViewCount(int postId);

    void insertPost(BoardPostsVO boardPost);

    int deletePost(int postId);

    int updatePost(int postId, String title, String content);
}

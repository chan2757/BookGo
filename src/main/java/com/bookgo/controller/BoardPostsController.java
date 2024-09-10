package com.bookgo.controller;

import com.bookgo.service.CommentService;
import com.bookgo.service.SiteUserService;
import com.bookgo.vo.BoardPostsVO;
import com.bookgo.service.BoardPostsService;
import com.bookgo.vo.CategoryVO;
import com.bookgo.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardPostsController {

    private static final Logger logger = LoggerFactory.getLogger(BoardPostsController.class);

    private final BoardPostsService boardPostsService;
    private final CommentService commentService;
    private final SiteUserService userService;

    @Autowired
    public BoardPostsController(BoardPostsService boardPostsService, CommentService commentService, SiteUserService userService) {
        this.boardPostsService = boardPostsService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/load")
    @ResponseBody
    public List<BoardPostsVO> getAllPosts() {
        return boardPostsService.getAllPosts();
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<CategoryVO> getAllCategories() {
        return boardPostsService.getAllCategories();
    }

    @GetMapping("/category/{categoryId}")
    @ResponseBody
    public List<BoardPostsVO> getPostsByCategory(@PathVariable Long categoryId) {
        return boardPostsService.getPostsByCategory(categoryId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<BoardPostsVO> searchPosts(@RequestParam String type, @RequestParam String content, Model model) {
        List<BoardPostsVO> posts;
        switch (type) {
            case "title":
                posts = boardPostsService.getPostsByTitle(content);
                break;
            case "username":
                posts = boardPostsService.getPostsByUsername(content);
                break;
            case "content":
                posts = boardPostsService.getPostsByContent(content);
                break;
            default:
                posts = boardPostsService.getAllPosts();
        }
        model.addAttribute("posts", posts);
        logger.debug("Model Attribute 'posts': {}", posts);
        return posts;
    }

    @GetMapping("/detail")
    public String getBoardDetail(@RequestParam("postId") int postId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("Fetching details for post ID: {}", postId);

        BoardPostsVO post = boardPostsService.getPostById(postId);
        List<CommentVO> comments = commentService.getCommentsByPostId((long) postId);

        System.out.println("게시글 상세 페이지 진입");

        if (post != null) {
            logger.debug("Post found: {}", post);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);
            if (userDetails != null) {
                model.addAttribute("currentUsername", userDetails.getUsername());
            }
            return "board/boarddetail";
        } else {
            logger.warn("Post not found for ID: {}", postId);
            return "error/404";
        }
    }

    @PostMapping("/incrementRecommend/{postId}")
    @ResponseBody
    public ResponseEntity<String> incrementRecommend(@PathVariable int postId) {
        try {
            boardPostsService.incrementRecommendCount(postId);
            return ResponseEntity.ok("추천수가 증가했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("추천수 증가 실패");
        }
    }

    // 파일 업로드 예시
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String filePath = "uploads/" + filename;  // 파일 저장 경로 수정 필요
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);
            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다.");
        } catch (IOException e) {
            logger.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    // 파일 다운로드 예시
    @GetMapping("/uploads/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            File file = new File("uploads/" + filename); // 파일 경로 수정 필요
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            logger.error("File download failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/write")
    public String contact() {
        logger.info("글쓰기 진입");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        logger.info("현재 로그인한 유저: {}", username);
        return "board/boardwrite";
    }

    @PostMapping("/delete/{postId}")
    public String deletePost(@PathVariable("postId") int postId) {
        logger.debug("Deleting post with ID: {}", postId);
        boolean isDeleted = boardPostsService.deletePost(postId);

        if (isDeleted) {
            logger.debug("Post deleted successfully.");
            return "redirect:/boardmain";
        } else {
            logger.warn("Failed to delete post with ID: {}", postId);
            return "error/404";
        }
    }

    @PostMapping("/update/{postId}")
    @ResponseBody
    public ResponseEntity<String> updatePost(@PathVariable("postId") int postId, @RequestBody Map<String, String> updatedData) {
        String updatedTitle = updatedData.get("title");
        String updatedContent = updatedData.get("content");

        boolean isUpdated = boardPostsService.updatePost(postId, updatedTitle, updatedContent);

        if (isUpdated) {
            return ResponseEntity.ok("수정 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }

    /*
    @GetMapping("/go")
    public String goToDetailPage(@RequestParam("postId") int postId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        logger.debug("Navigating to detail page for post ID: {}", postId);
        BoardPostsVO post = boardPostsService.getPostById(postId);
        List<CommentVO> comments = commentService.getCommentsByPostId((long) postId);



        if (post != null) {
            logger.debug("Post found: {}", post);
            model.addAttribute("post", post);
            model.addAttribute("comments", comments);

            if (userDetails != null) {
                model.addAttribute("currentUsername", userDetails.getUsername());
            }

            return "board/boarddetail";
        } else {
            logger.warn("Post not found for ID: {}", postId);
            return "error/404";
        }
    }

     */

    @PostMapping("/{postId}/comment")
    @ResponseBody
    public ResponseEntity<String> addComment(@PathVariable("postId") Long postId,
                                             @RequestParam("content") String content,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            CommentVO comment = new CommentVO();
            comment.setPostId(postId);
            comment.setUsername(userDetails.getUsername());
            comment.setContent(content);
            commentService.addComment(comment);
            return ResponseEntity.ok("댓글이 성공적으로 등록되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
    }

    @GetMapping("/myboard")
    public String getMyPosts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            Long userId = (long) userService.getUserIdByUsername(username);

            logger.debug("Fetching posts for user ID: {}", userId);

            List<BoardPostsVO> posts = boardPostsService.getPostsByUserId(userId);

            // posts 리스트를 출력하여 확인
            if (posts != null && !posts.isEmpty()) {
                posts.forEach(post -> logger.debug("Post ID: {}, Title: {}", post.getPostId(), post.getTitle()));
            } else {
                logger.debug("No posts found for user ID: {}", userId);
            }

            model.addAttribute("posts", posts);
            model.addAttribute("username", username);
        } else {
            logger.warn("Unauthorized access to myboard.");
            return "redirect:/user/login";
        }
        return "myown/myboard"; // 새로운 템플릿 경로
    }
}

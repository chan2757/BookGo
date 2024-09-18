package com.bookgo.controller;

import com.bookgo.service.BookDetailService;
import com.bookgo.service.FileService;
import com.bookgo.vo.BoardPostsVO;
import com.bookgo.vo.BookDetailVO;
import com.bookgo.vo.BookDpVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bookgo.service.SiteUserService;
import org.springframework.web.multipart.MultipartFile;
import com.bookgo.service.BoardPostsService;

import java.util.List;

@Controller
@RequestMapping("/")
public class BookGoController {
	private static final Logger logger = LoggerFactory.getLogger(BookGoController.class);

	private final SiteUserService userService;

	 // BookService를 통해 책 목록을 조회

	private final BookDetailService bookDetailService;

	private final FileService fileService; // FileService 선언

	public BookGoController(SiteUserService userService, BoardPostsService boardPostsService, FileService fileService, BookDetailService bookDetailService) {
		this.userService = userService;
        this.boardPostsService = boardPostsService;
        this.bookDetailService = bookDetailService;
        this.fileService = new FileService(); // FileService 객체 생성
    }

	private final BoardPostsService boardPostsService;




    // 메인 페이지 매핑 메서드


	@GetMapping("/shop")
	public String booklist() {
		logger.info("리스트 페이지 진입");
		return "bookgo/booksearch"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/map")
	public String map() {
		logger.info("도서관지도 페이지 진입");

		return "bookgo/map"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/login")
	public String login() {
		logger.info("로그인 페이지 진입");
		return "user/login"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/myinfo")
	public String myinfo() {
		logger.info("나의 정보 페이지 진입");
		return "user/infoDetail"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/signup")
	public String signup() {
		logger.info("회원가입 페이지 진입");
		return "user/signup"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/mypagemenu")
	public String mypagemenu() {
		logger.info("마이페이지메뉴 페이지 진입");
		return "user/mypage"; // templates/bookgo/index.html로 매핑
	}

	@GetMapping("/contact")
	public String contact() {
		logger.info("고객센터 페이지 진입");
		return "voc/vocMain";
	}

	// FAQ 게시판 페이지로 이동
	@GetMapping("/voc/vocFAQ")
	public String vocFAQ( ) {

		return "voc/vocFAQ";
	}

	// FAQ 게시판 페이지로 이동
	@GetMapping("/voc/inquiry")
	public String QnA( ) {

		return "qna/qindex";
	}

	@GetMapping("/boardmain")
	public String boardmain(@AuthenticationPrincipal UserDetails userDetails, Model model) {

		if (userDetails != null) {
			String username = userDetails.getUsername();
			model.addAttribute("username", username);


			Integer userId = userService.getUserIdByUsername(username);
			model.addAttribute("userId", userId);
		} else {

			model.addAttribute("username", null);
			model.addAttribute("userId", null);
		}


		return "board/boardpost";
	}
	@PostMapping("/board/write")
	public String writePost(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("categoryId") long categoryId,
			@RequestParam("title") String title,
			@RequestParam("content") String content,
			@RequestParam("filenames") MultipartFile[] files,
			Model model) {

		Logger logger = LoggerFactory.getLogger(getClass());
		logger.debug("writePost 메소드 호출됨");
		logger.debug("요청 파라미터 - categoryId: {}, title: {}, content: {}", categoryId, title, content);

		if (userDetails != null) {
			String username = userDetails.getUsername();
			Long userId = (long) userService.getUserIdByUsername(username);
			boolean isAdmin = userDetails.getAuthorities().stream()
					.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

			logger.debug("로그인 사용자 - username: {}, userId: {}, isAdmin: {}", username, userId, isAdmin);


			long noticeCategoryId = 1L;


			if (categoryId == noticeCategoryId && !isAdmin) {
				logger.warn("관리자 권한이 없는 사용자가 공지글을 작성하려고 시도함 - username: {}", username);
				model.addAttribute("message", "공지글은 관리자만 작성할 수 있습니다.");
				return "redirect:/boardmain";
			}

			BoardPostsVO boardPost = new BoardPostsVO();
			boardPost.setCategoryId(categoryId);
			boardPost.setUserId(userId);
			boardPost.setUsername(username);
			boardPost.setTitle(title);
			boardPost.setContent(content);


			try {
				StringBuilder fileNames = new StringBuilder();

				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						String savedFilePath = fileService.saveFile(file);
						fileNames.append(savedFilePath).append(",");
						logger.debug("파일이 성공적으로 저장되었습니다: {}", savedFilePath);
					}
				}


				if (fileNames.length() > 0) {
					boardPost.setFilename(fileNames.toString().replaceAll(",$", ""));
				}

				boardPostsService.insertPost(boardPost);
				logger.debug("게시글이 데이터베이스에 성공적으로 저장되었습니다.");
				model.addAttribute("message", "게시글이 성공적으로 작성되었습니다.");

			} catch (Exception e) {
				logger.error("게시글 작성 중 오류 발생", e);
				model.addAttribute("message", "게시글 작성 중 오류가 발생했습니다.");
			}
		} else {
			logger.warn("로그인하지 않은 사용자에 의해 게시글 작성 시도됨");
			model.addAttribute("message", "로그인 후 게시글을 작성할 수 있습니다.");
		}

		return "redirect:/boardmain";
	}

	@GetMapping("/bookcategory/{categoryId}")
	public String getBooksByCategory(@PathVariable("categoryId") String categoryId,
									 @RequestParam(defaultValue = "1") int page,
									 Model model) {
		List<BookDpVO> books = bookDetailService.getBookDetailByCategoryId(categoryId);

		int maxResults = 40;
		int totalResults = books.size();
		int totalPages = (int) Math.ceil((double) totalResults / maxResults);


		int start = (page - 1) * maxResults;

		List<BookDpVO> paginatedBooks = books.subList(start, Math.min(start + maxResults, totalResults));

		model.addAttribute("books", paginatedBooks);
		model.addAttribute("from", "category");
		model.addAttribute("query", "");
		model.addAttribute("queryType", "");
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("categoryId", categoryId);

		return "bookgo/bookList";
	}



}

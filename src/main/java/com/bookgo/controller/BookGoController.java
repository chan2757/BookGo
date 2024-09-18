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
		return "voc/vocMain"; // templates/bookgo/index.html로 매핑
	}

	// FAQ 게시판 페이지로 이동
	@GetMapping("/voc/vocFAQ")
	public String vocFAQ( ) {
		// 필요한 경우, 페이지 로딩 시 필요한 데이터를 Model에 추가할 수 있습니다.
		return "voc/vocFAQ"; // templates 폴더에 있는 vocFAQ.html로 연결
	}

	// FAQ 게시판 페이지로 이동
	@GetMapping("/voc/inquiry")
	public String QnA( ) {
		// 필요한 경우, 페이지 로딩 시 필요한 데이터를 Model에 추가할 수 있습니다.
		return "qna/qindex"; // templates 폴더에 있는 vocFAQ.html로 연결
	}

	@GetMapping("/boardmain")
	public String boardmain(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		// 사용자 정보가 로그인 되어 있는지 확인
		if (userDetails != null) {
			String username = userDetails.getUsername();
			model.addAttribute("username", username);

			// 사용자 이름을 통해 사용자 ID를 가져옵니다.
			Integer userId = userService.getUserIdByUsername(username);
			model.addAttribute("userId", userId);
		} else {
			// 로그인하지 않은 경우
			model.addAttribute("username", null);
			model.addAttribute("userId", null);
		}

		// 필요한 경우, 페이지 로딩 시 필요한 데이터를 Model에 추가할 수 있습니다.
		return "board/boardpost"; // templates/board/boardpost.html로 연결
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
					.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")); // 사용자 권한 확인

			logger.debug("로그인 사용자 - username: {}, userId: {}, isAdmin: {}", username, userId, isAdmin);

			// 공지 카테고리 번호 (예시로 1번을 공지로 설정)
			long noticeCategoryId = 1L;

			// 공지 카테고리일 때 관리자만 작성 가능
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

			// 여러 파일 업로드 처리
			try {
				StringBuilder fileNames = new StringBuilder(); // 파일명들을 저장할 StringBuilder

				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						String savedFilePath = fileService.saveFile(file); // 각 파일을 저장하고 경로를 반환
						fileNames.append(savedFilePath).append(","); // 파일 경로를 추가하고 쉼표로 구분
						logger.debug("파일이 성공적으로 저장되었습니다: {}", savedFilePath);
					}
				}

				// 모든 파일의 경로를 설정 (필요에 따라 쉼표 제거 후 설정)
				if (fileNames.length() > 0) {
					boardPost.setFilename(fileNames.toString().replaceAll(",$", "")); // 마지막 쉼표 제거
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
	public String getBooksByCategory(@PathVariable("categoryId") String categoryId, Model model) {
		List<BookDpVO> books = bookDetailService.getBookDetailByCategoryId(categoryId);
		model.addAttribute("books", books);

		int totalPages = (int) Math.ceil((double) books.size() / 35);

		// 검색어가 없으므로 기본적으로 빈 문자열 설정
		model.addAttribute("query", "");
		model.addAttribute("queryType", "");

		// 페이지네이션 관련 기본 값 추가 (필요하다면)
		model.addAttribute("currentPage", 1);
		model.addAttribute("totalPages", totalPages);


		return "bookgo/bookList";  // 카테고리별 책 목록 페이지
	}



}

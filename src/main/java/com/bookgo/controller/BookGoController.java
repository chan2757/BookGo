package com.bookgo.controller;

import com.bookgo.vo.BoardPostsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookgo.service.SiteUserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.bookgo.service.BoardPostsService;

@Controller
@RequestMapping("/")
public class BookGoController {
	private static final Logger logger = LoggerFactory.getLogger(BookGoController.class);

	private final SiteUserService userService;

	public BookGoController(SiteUserService userService, BoardPostsService boardPostsService) {
		this.userService = userService;
        this.boardPostsService = boardPostsService;
    }

	private final BoardPostsService boardPostsService;




    // 메인 페이지 매핑 메서드
	@GetMapping("/index")
	public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails != null) {
			// 로그인된 사용자의 이름(또는 사용자 정보를 가져올 수 있습니다)
			model.addAttribute("username", userDetails.getUsername());

		}
		return "bookgo/index"; // templates/bookgo/index.html로 매핑
	}

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
			@RequestParam("filename") MultipartFile file,
			Model model) {

		// Logger 객체 생성
		Logger logger = LoggerFactory.getLogger(getClass());

		logger.debug("writePost 메소드 호출됨");
		logger.debug("요청 파라미터 - categoryId: {}, title: {}, content: {}", categoryId, title, content);

		if (userDetails != null) {
			String username = userDetails.getUsername();
			Long  userId = (long) userService.getUserIdByUsername(username);

			logger.debug("로그인 사용자 - username: {}, userId: {}", username, userId);

			// 게시글을 저장할 VO 객체 생성
			BoardPostsVO boardPost = new BoardPostsVO();
			boardPost.setCategoryId(categoryId);
			boardPost.setUserId(userId);
			boardPost.setUsername(username);
			boardPost.setTitle(title);
			boardPost.setContent(content);

			// 파일 처리 (필요한 경우)
			if (file != null && !file.isEmpty()) {
				String filename = file.getOriginalFilename();
				boardPost.setFilename(filename);

				logger.debug("업로드된 파일 이름: {}", filename);

				// 파일 저장 로직을 추가할 수 있습니다
				// 예: 파일을 서버에 저장하는 코드
			}

			try {
				// 게시글을 데이터베이스에 저장
				boardPostsService.insertPost(boardPost);
				logger.debug("게시글이 데이터베이스에 성공적으로 저장되었습니다.");
				// 성공적으로 게시글이 작성되었음을 알리는 메시지
				model.addAttribute("message", "게시글이 성공적으로 작성되었습니다.");
			} catch (Exception e) {
				logger.error("게시글 작성 중 오류 발생", e);
				model.addAttribute("message", "게시글 작성 중 오류가 발생했습니다.");
			}
		} else {
			logger.warn("로그인하지 않은 사용자에 의해 게시글 작성 시도됨");
			model.addAttribute("message", "로그인 후 게시글을 작성할 수 있습니다.");
		}

		return "redirect:/boardmain"; // 게시글 목록 페이지로 리디렉션
	}


}

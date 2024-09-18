package com.bookgo.config;

import com.bookgo.service.CategoryService;
import com.bookgo.vo.BookCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;

    // 로그인된 사용자 또는 관리자 이름을 모델에 추가
    @ModelAttribute("username")
    public String addUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "관리자: " + auth.getName(); // 관리자인 경우 "관리자: 사용자이름" 형태로 반환
                }
            }
            return "사용자: " + auth.getName(); // 일반 사용자인 경우 "사용자: 사용자이름" 형태로 반환
        }
        return null; // 로그인되지 않은 경우 null 반환
    }

    @ModelAttribute("bookcategories")
    public List<BookCategoryVO> addCategoriesToModel() {
        List<BookCategoryVO> BookCategorys = categoryService.getAllCategories();
        System.out.println(BookCategorys);
        return BookCategorys;
    }
}

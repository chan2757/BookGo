package com.bookgo.controller;

import com.bookgo.service.QnAService;
import com.bookgo.vo.QnAVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/qna")
public class QnAController {

    @Autowired
    private QnAService qnaService;

    @GetMapping("/new")
    public String createQnAForm(Model model) {
        model.addAttribute("qna", new QnAVO());
        return "qna/Qwrite";
    }

    @PostMapping("/insert")
    public String insertQnA(@ModelAttribute QnAVO qna, MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            // 파일 처리 로직 추가
            qna.setFilename(file.getOriginalFilename());
        }
        qna.setWriteDate(LocalDateTime.now());
        qnaService.saveQnA(qna);
        redirectAttributes.addFlashAttribute("message", "질문이 성공적으로 저장되었습니다.");
        return "redirect:/qna/";
    }
}

package com.bookgo.service;

import com.bookgo.mapper.InquiryMapper;
import com.bookgo.vo.InquiryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InquiryService {

    @Autowired
    private InquiryMapper inquiryMapper;

    // 사용자 ID로 문의 목록 조회
    public List<InquiryVO> getInquiriesByUserId(int userId) {
        return inquiryMapper.getInquiriesByUserId(userId);
    }

    // 문의 생성
    public void createInquiry(InquiryVO inquiry) {
        inquiryMapper.createInquiry(inquiry);
    }

    // 모든 문의 조회 및 상태 설정
    public List<InquiryVO> getAllInquiries() {
        List<InquiryVO> inquiries = inquiryMapper.getAllInquiries();

        // 상태 설정 로직 추가
        for (InquiryVO inquiry : inquiries) {
            if (inquiry.getReplyContent() == null || inquiry.getReplyContent().isEmpty()) {
                inquiry.setStatus("답변준비중");
            } else {
                inquiry.setStatus("답변완료");
            }
        }
        return inquiries;
    }

    // 답변 저장
    public void saveReply(int inquiryId, int adminId, String replyContent) {
        inquiryMapper.saveReply(inquiryId, adminId, replyContent); // 답변 저장
    }

    /**
     * INQUIRY 테이블과 INQUIRY_REPLY 테이블의 행 수가 같은지 확인하는 메소드.
     *
     * @return 같으면 true, 다르면 false
     */
    public boolean isInquiryAndReplyCountEqual() {
        int inquiryCount = inquiryMapper.countInquiries();
        int replyCount = inquiryMapper.countReplies();

        // 두 테이블의 행 수가 같으면 true, 다르면 false 반환
        return inquiryCount == replyCount;
    }
}

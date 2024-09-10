package com.bookgo.mapper;

import com.bookgo.vo.InquiryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InquiryMapper {

    // 사용자 ID로 문의 목록 조회
    List<InquiryVO> getInquiriesByUserId(@Param("userId") int userId);

    // 문의 생성
    void createInquiry(InquiryVO inquiry);


    List<InquiryVO> getAllInquiries();

    void saveReply(int inquiryId, int adminId, String replyContent);

    int countInquiries();

    int countReplies();
}

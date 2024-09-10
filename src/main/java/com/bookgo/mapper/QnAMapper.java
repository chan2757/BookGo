package com.bookgo.mapper;

import com.bookgo.vo.QnAVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QnAMapper {
    void insertQnA(QnAVO qna);
}

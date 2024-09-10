package com.bookgo.service;

import com.bookgo.mapper.QnAMapper;
import com.bookgo.vo.QnAVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnAService {

    @Autowired
    private QnAMapper qnaMapper;

    public void saveQnA(QnAVO qna) {
        qnaMapper.insertQnA(qna);
    }
}

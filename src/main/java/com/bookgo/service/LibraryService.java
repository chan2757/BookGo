package com.bookgo.service;


import com.bookgo.mapper.LibraryInfoMapper;
import com.bookgo.vo.LibraryInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {

    @Autowired
    private LibraryInfoMapper libraryInfoMapper;

    // 전체 도서관 목록을 가져오는 메서드
    public List<LibraryInfoVO> getAllLibraries() {
        return libraryInfoMapper.getAllLibraries();
    }

    // 특정 도서관 하나를 가져오는 메서드
    public LibraryInfoVO getOneLibrary(int id) {
        return libraryInfoMapper.getOneLibrary(id); // ID 인자를 제공하여 메서드를 호출합니다.
    }
}


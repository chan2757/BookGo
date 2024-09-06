package com.bookgo.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.bookgo.vo.LibraryInfoVO;
import java.util.List;

@Mapper
public interface LibraryInfoMapper {

    // 도서관 전체 조회
    List<LibraryInfoVO> getAllLibraries();

    // 특정 도서관 조회
    LibraryInfoVO getOneLibrary(int id);
}

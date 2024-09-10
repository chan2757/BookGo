package com.bookgo.mapper;

import com.bookgo.vo.SiteUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SiteUserMapper {

    // 사용자 등록
    void insertUser(SiteUserVO user);

    // username으로 사용자 조회
    SiteUserVO getUserByUsername(String username);

    // 이메일로 사용자 조회
    SiteUserVO getUserByEmail(String email);

    // 이메일로 아이디 조회
    String getUsernameByEmail(String email);

    // 비밀번호 업데이트
    void updatePassword(@Param("username") String username, @Param("password") String password);

    // 비밀번호 조회
    String getPasswordByUsername(String username);

    void updateUserField(Map<String, Object> updateData);

    int getUserIdByUsername(String username);

    List<SiteUserVO> getAllUsers();

    int countByEmail(String email);

    int countByUsername(String username);
}

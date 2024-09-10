package com.bookgo.service;

import com.bookgo.mapper.SiteUserMapper;
import com.bookgo.vo.SiteUserVO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SiteUserMapper userMapper;

    public CustomUserDetailsService(SiteUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUserVO user = userMapper.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username);
        }
        String role = user.getRole(); // 데이터베이스에서 "USER" 또는 "ADMIN" 등의 역할을 가져온다고 가정

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(role.toUpperCase()) // 가져온 역할을 대문자로 설정
                .build();
    }

}

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
        // SiteUserVO를 UserDetails로 변환하여 반환 (필요한 변환 코드 추가)
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER") // 사용자 역할 설정 (예: USER, ADMIN)
                .build();
    }
}

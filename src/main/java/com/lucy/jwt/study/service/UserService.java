package com.lucy.jwt.study.service;

import java.util.Collections;
import java.util.Optional;

import com.lucy.jwt.study.common.exception.AdminException;
import com.lucy.jwt.study.common.exception.DuplicateMemberException;
import com.lucy.jwt.study.common.exception.NotFoundMemberException;
import com.lucy.jwt.study.common.util.SecurityUtil;
import com.lucy.jwt.study.dto.UserDto;
import com.lucy.jwt.study.entity.Authority;
import com.lucy.jwt.study.entity.User;
import com.lucy.jwt.study.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto signup(UserDto userDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }

    @Transactional
    public boolean leave(UserDto userDto) {
        if (userDto.getUsername().equals("admin")) {
            throw new AdminException("관리자 계정은 삭제가 불가능합니다.");
        }

        Optional<User> list = userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername());

        if (list.isEmpty()) {
            throw new DuplicateMemberException("가입되어 있지 않은 유저입니다.");
        } else {
            User user = list.get();

            User inputUser = User.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .nickname(userDto.getNickname())
                    .build();
            
            // 비밀번호 암호화 변경하기
            if(
                    !user.getUsername().equals(inputUser.getUsername())
                    || !user.getNickname().equals(inputUser.getNickname())
            ) throw new NotFoundMemberException("유저 정보가 다릅니다.");

            user.setActivated(false);
            userRepository.save(user);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}
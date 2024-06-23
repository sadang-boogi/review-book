package com.rebook.user.service;

import com.rebook.user.domain.Role;
import com.rebook.user.domain.SocialType;
import com.rebook.user.domain.UserEntity;
import com.rebook.user.service.dto.LoggedInUser;
import com.rebook.user.service.dto.SocialUserCreateCommand;
import com.rebook.user.repository.UserRepository;
import com.rebook.user.service.dto.UserCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoggedInUser createSocialUser(SocialUserCreateCommand command) {
        String socialId = command.getSocialId();
        Optional<UserEntity> existingUser = userRepository.findBySocialId(socialId);

        //존재하는 회원일 경우
        if (existingUser.isPresent()) {
            return LoggedInUser.fromEntity(existingUser.get());
        }

        //아닐 경우 신규 DB에 저장
        UserEntity newUser = UserEntity.builder()
                .email(command.getEmail())
                .nickname(command.getName())
                .socialType(SocialType.valueOf(command.getSocialType()))
                .socialId(command.getSocialId())
                .role(Role.USER)
                .build();
        return LoggedInUser.fromEntity(userRepository.save(newUser));
    }

    public LoggedInUser createUser(UserCreateCommand command) {
        String email = command.getEmail();
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return LoggedInUser.fromEntity(existingUser.get());
        }

        String encryptedPassword = passwordEncoder.encode(command.getPassword());
        UserEntity newUser = UserEntity.builder()
                .email(command.getEmail())
                .nickname(command.getNickname())
                .password(encryptedPassword)
                .role(Role.USER)
                .build();
        return LoggedInUser.fromEntity(userRepository.save(newUser));
    }
}

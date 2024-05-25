package com.rebook.user.service;

import com.rebook.user.domain.Role;
import com.rebook.user.domain.SocialType;
import com.rebook.user.domain.UserEntity;
import com.rebook.user.service.dto.LoggedInUser;
import com.rebook.user.service.dto.UserCommand;
import com.rebook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public LoggedInUser createUser(UserCommand userCommand) {
        String socialId = userCommand.getSocialId();
        Optional<UserEntity> existingUser = userRepository.findBySocialId(socialId);

        //이전에 소셜 로그인 했던 user인 경우 db user를 불러옴
        if (existingUser.isPresent()) {
            return LoggedInUser.fromEntity(existingUser.get());
        }

        //아닐 경우 신규 DB에 저장
        UserEntity newUser = UserEntity.builder()
                .email(userCommand.getEmail())
                .nickname(userCommand.getName())
                .socialType(SocialType.valueOf(userCommand.getSocialType()))
                .socialId(userCommand.getSocialId())
                .role(Role.USER)
                .build();
        return LoggedInUser.fromEntity(userRepository.save(newUser));
    }
}

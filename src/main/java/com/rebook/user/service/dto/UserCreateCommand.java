package com.rebook.user.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateCommand {
    String nickname;
    String email;
    String password;
}

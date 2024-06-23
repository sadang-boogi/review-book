package com.rebook.user.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SocialUserCreateCommand {
    String name;
    String email;
    String socialId;
    String socialType;
}

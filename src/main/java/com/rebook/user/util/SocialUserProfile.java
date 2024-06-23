package com.rebook.user.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SocialUserProfile {
    private String name;
    private String email;
    private String socialId;
    private String socialType;
}

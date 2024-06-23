package com.rebook.user.service;

import com.rebook.user.config.OAuthServiceProvider;
import com.rebook.user.service.dto.LoggedInUser;
import com.rebook.user.service.dto.SocialUserCreateCommand;
import com.rebook.user.util.OAuthService;
import com.rebook.user.util.SocialUserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final UserService userService;
    private final OAuthServiceProvider oAuthServiceProvider;

    public LoggedInUser socialLogin(String code, String registrationId) {

        OAuthService oauthProvider = oAuthServiceProvider.getService(registrationId.toLowerCase());
        if (oauthProvider == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
        String accessToken = oauthProvider.getAccessToken(code);
        SocialUserProfile userProfile = oauthProvider.getUserProfile(accessToken);
        return userService.createSocialUser(
                SocialUserCreateCommand.builder()
                        .email(userProfile.getEmail())
                        .name(userProfile.getName())
                        .socialId(userProfile.getSocialId())
                        .socialType(userProfile.getSocialType())
                        .build()
        );
    }

    public String getAuthorizationUrl(String registrationId) {
        OAuthService oauthProvider = oAuthServiceProvider.getService(registrationId.toLowerCase());
        if (oauthProvider == null) {
            throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
        }
        return oauthProvider.getAuthorizationUrl();
    }
}

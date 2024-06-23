package com.rebook.user.util;


public interface OAuthService {
    String getAuthorizationUrl();

    String getAccessToken(String authorizationCode);

    SocialUserProfile getUserProfile(String accessToken);
}

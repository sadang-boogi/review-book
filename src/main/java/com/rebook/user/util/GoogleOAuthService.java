package com.rebook.user.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class GoogleOAuthService implements OAuthService {

    private final GoogleOAuthProperties googleOAuthProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getAuthorizationUrl() {
        return googleOAuthProperties.getEndPoint() +
                "?client_id=" + googleOAuthProperties.getClientId() +
                "&redirect_uri=" + googleOAuthProperties.getRedirectUri() +
                "&response_type=code" +
                "&scope=" + googleOAuthProperties.getScopes();
    }


    @Override
    public String getAccessToken(String authorizationCode) {

        String clientId = googleOAuthProperties.getClientId();
        String clientSecret = googleOAuthProperties.getClientSecret();
        String redirectUri = googleOAuthProperties.getRedirectUri();
        String tokenUri = googleOAuthProperties.getTokenUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", this.sanitizeAuthCode(authorizationCode));
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    /**
     * Google OAuth 인증 코드가 URL Encode 되어 있으므로 디코딩하여 반환합니다.
     */
    private String sanitizeAuthCode(String authCode) {
        return URLDecoder.decode(authCode, StandardCharsets.UTF_8);
    }


    private JsonNode getUserResource(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(googleOAuthProperties.getUserInfoUri(), HttpMethod.GET, entity, JsonNode.class).getBody();
    }


    @Override
    public SocialUserProfile getUserProfile(String accessToken) {
        JsonNode userResource = getUserResource(accessToken);
        String id = userResource.get("sub").asText();
        String email = userResource.get("email").asText();
        String name = userResource.get("name").asText();
        return SocialUserProfile.builder()
                .name(name)
                .email(email)
                .socialId(id)
                .socialType("GOOGLE").build();
    }
}

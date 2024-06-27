package com.rebook.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebook.jwt.service.JwtProperties;
import com.rebook.user.service.dto.LoggedInUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private String tokenPrefix = "Bearer ";
    private ObjectMapper objectMapper = new ObjectMapper();

    public boolean isIncludeTokenPrefix(String header) {
        return header.split(" ")[0].equals(tokenPrefix.trim());
    }

    public String extractTokenFromHeader(String header) {
        return header.replace(tokenPrefix, "");
    }

    public String createToken(LoggedInUser loggedInUser, Instant currentDate) {
        return JWT.create()
                .withSubject(String.valueOf(loggedInUser.getUserId()))
                .withExpiresAt(currentDate.plusSeconds(jwtProperties.getTokenValidityInSeconds()))
                .withClaim("email", loggedInUser.getEmail())
                .withClaim("username", loggedInUser.getName())
                .sign(Algorithm.HMAC512(jwtProperties.getSecret()));
    }

    public boolean isTokenExpired(String token) {
        Instant expiredAt = JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                .build().verify(token)
                .getExpiresAtAsInstant();
        return expiredAt.isBefore(Instant.now());
    }

    public boolean isTokenNotManipulated(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return false;
        }
    }

    public LoggedInUser extractUserFromToken(String token) {
        String payload = JWT.decode(token)
                .getPayload();

        byte[] decodedBytes = Base64.getDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);

        return parseUserFromJwt(decodedPayload);
    }

    private LoggedInUser parseUserFromJwt(String decodedPayload) {
        try {
            LinkedHashMap<String, Object> payloadMap = objectMapper.readValue(decodedPayload, LinkedHashMap.class);
            Long userId = Long.parseLong((String) payloadMap.get("sub"));
            String email = (String) payloadMap.get("email");
            String username = (String) payloadMap.get("username");
            return new LoggedInUser(userId, email, username);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public Long extractSubjectId(String authorizationParameters) {
        String payload = JWT.decode(authorizationParameters)
                .getPayload();

        byte[] decodedBytes = Base64.getDecoder().decode(payload);
        String decodedPayload = new String(decodedBytes);

        try {
            LinkedHashMap<String, Object> payloadMap = objectMapper.readValue(decodedPayload, LinkedHashMap.class);
            return Long.parseLong((String) payloadMap.get("sub"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
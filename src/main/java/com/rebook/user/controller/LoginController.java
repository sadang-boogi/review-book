package com.rebook.user.controller;

import com.rebook.user.controller.response.JwtResponse;
import com.rebook.user.service.LoginService;
import com.rebook.user.service.dto.JwtUtil;
import com.rebook.user.service.dto.LoggedInUser;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/login/oauth2", produces = "application/json")
public class LoginController {
    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "소셜 로그인",
            description = "소셜 프로바이더를 통해 로그인하고 JWT 토큰을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "JWT 토큰을 반환합니다.")
            }
    )
    @PostMapping("/{provider}")
    public ResponseEntity<JwtResponse> socialLogin(
            @RequestBody String code, @PathVariable String provider) {
        LoggedInUser loggedInUser = loginService.socialLogin(code, provider);
        String token = jwtUtil.createToken(loggedInUser, Instant.now());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/{provider}")
    public ResponseEntity<JwtResponse> socialLoginTest(
            @RequestParam String code, @PathVariable String provider) {
        LoggedInUser loggedInUser = loginService.socialLogin(code, provider);
        String token = jwtUtil.createToken(loggedInUser, Instant.now());
        return ResponseEntity.ok(new JwtResponse(token));
    }


}
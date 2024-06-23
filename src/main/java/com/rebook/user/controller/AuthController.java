package com.rebook.user.controller;

import com.rebook.jwt.JwtUtil;
import com.rebook.user.controller.request.SignupRequestSchema;
import com.rebook.user.controller.response.JwtResponse;
import com.rebook.user.service.UserService;
import com.rebook.user.service.dto.LoggedInUser;
import com.rebook.user.service.dto.UserCreateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "이메일로 회원가입",
            description = "이메일로 회원가입을 합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공 시 JWT 토큰을 반환합니다.")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> signup(@RequestBody SignupRequestSchema body) {
        LoggedInUser loggedInUser = userService.createUser(
                UserCreateCommand
                        .builder()
                        .email(body.getEmail())
                        .nickname(body.getNickname())
                        .password(body.getPassword())
                        .build()
        );
        String token = jwtUtil.createToken(loggedInUser, Instant.now());
        return ResponseEntity.status(201).body(new JwtResponse(token));
    }
}

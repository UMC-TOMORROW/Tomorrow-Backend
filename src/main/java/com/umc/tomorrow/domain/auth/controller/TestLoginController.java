package com.umc.tomorrow.domain.auth.controller;


import com.umc.tomorrow.domain.auth.dto.TestLoginRequest;
import com.umc.tomorrow.domain.auth.dto.TestLoginResponse;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.global.common.base.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class TestLoginController {

    private final JWTUtil jwtUtil;


    @PostMapping("/test-login")
    public BaseResponse<TestLoginResponse> testLogin(@RequestBody TestLoginRequest request) {


        // 만료시간 1시간 (3600000ms)
        String token = jwtUtil.createJwt(
                request.getUserId(),
                request.getName(),
                request.getUsername(),
                request.getRole(),
                1000L * 60 * 60
        );

        return BaseResponse.onSuccess(new TestLoginResponse(token));
    }
}


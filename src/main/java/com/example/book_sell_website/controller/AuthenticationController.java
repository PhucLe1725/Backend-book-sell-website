package com.example.book_sell_website.controller;

import com.example.book_sell_website.dto.Login_logout_register.logInDTO;
import com.example.book_sell_website.dto.base.response.ApiResponse;
import com.example.book_sell_website.dto.authen.request.IntrospectRequest;
import com.example.book_sell_website.dto.authen.request.LogoutRequest;
import com.example.book_sell_website.dto.authen.request.RefreshRequest;
import com.example.book_sell_website.dto.authen.response.AuthenticationResponse;
import com.example.book_sell_website.dto.authen.response.IntrospectResponse;
import com.example.book_sell_website.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    final AuthenticationService authenticationService;

    @PostMapping("/Login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody logInDTO request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/Introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/Logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws JOSEException, ParseException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/Refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws JOSEException, ParseException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }
}
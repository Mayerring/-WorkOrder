package com.example.workorder.api.service;

import com.example.workorder.api.dto.ValidateTokenResult;

public interface AuthApi {
    ValidateTokenResult validateToken(String token);
}
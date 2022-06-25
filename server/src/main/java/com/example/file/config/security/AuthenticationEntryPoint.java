package com.example.file.config.security;

import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Status;
import com.example.file.common.utli.WebUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class  AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws JsonProcessingException {
        WebUtils.renderString(response, ApiResponse.ofStatus(Status.USER_LOGIN_ERROR));
    }
}

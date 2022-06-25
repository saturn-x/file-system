package com.example.file.config.security;

import com.example.file.common.resp.ApiResponse;
import com.example.file.common.resp.Status;
import com.example.file.common.utli.WebUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserAuthAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws JsonProcessingException {
        WebUtils.renderString(response, ApiResponse.ofStatus(Status.AUTHORITY_ERROR));
    }
}

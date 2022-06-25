package com.example.file.common.utli;

import com.example.file.common.resp.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class WebUtils {

    public static void renderString(HttpServletResponse response, String string) {
        try{
            response.resetBuffer();
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.write(string);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void renderString(HttpServletResponse response, ApiResponse ofStatus) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(ofStatus);
        try{
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

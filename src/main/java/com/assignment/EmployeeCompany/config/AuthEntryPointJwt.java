package com.assignment.EmployeeCompany.config;

import com.assignment.EmployeeCompany.entity.ResponseMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {



    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());


        response.setContentType("application/json");
        response.setStatus(403);
        ResponseEntity<ResponseMessage> entity = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage(Boolean.FALSE, "Not authorized to access"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(),entity.getBody());

    }
}
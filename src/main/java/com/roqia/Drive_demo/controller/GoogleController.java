package com.roqia.Drive_demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/google")
public class GoogleController {
    @GetMapping("/link")
    public void linkGoogleAccount(HttpServletResponse response)  {

        try {
            response.sendRedirect("/oauth2/authorization/google");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

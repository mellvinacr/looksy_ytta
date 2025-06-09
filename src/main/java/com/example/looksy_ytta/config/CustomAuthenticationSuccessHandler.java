package com.example.looksy_ytta.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component 
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/"; // URL fallback default

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Periksa peran pengguna
        boolean isAdmin = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authorities.stream()
                                   .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (isAdmin) {
            redirectUrl = "/admin/dashboard";
        } else if (isUser) {
            redirectUrl = "/user/dashboard";
        }

        // Redirect ke URL yang ditentukan berdasarkan peran
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}

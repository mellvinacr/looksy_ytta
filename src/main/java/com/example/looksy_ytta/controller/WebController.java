package com.example.looksy_ytta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Make sure this annotation is present
public class WebController {

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Maps to src/main/resources/templates/register.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Maps to src/main/resources/templates/login.html
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login"; // Redirects root to login page
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin_dashboard"; // Maps to src/main/resources/templates/admin_dashboard.html
    }

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user_dashboard"; // Maps to src/main/resources/templates/user_dashboard.html
    }
}

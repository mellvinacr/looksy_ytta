package com.example.looksy_ytta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String homePage() {
        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin_dashboard";
    }

    @GetMapping("/admin/user")
    public String adminUser() {
        return "admin_user"; // New template for admin user management
    }


    @GetMapping("/admin/products")
    public String adminProductManagement() {
        return "admin_products"; // New template for product management
    }

    @GetMapping("/admin/orders")
    public String adminOrderManagement() {
        return "admin_orders"; // New template for order management
    }

    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user_dashboard";
    }

    @GetMapping("/user/products")
    public String userProductCatalog() {
        return "user_products"; // New template for product catalog
    }

    @GetMapping("/user/cart")
    public String userCart() {
        return "user_cart"; // New template for shopping cart
    }

    @GetMapping("/user/orders")
    public String userOrders() {
        return "user_orders"; // New template for user's orders
    }
}
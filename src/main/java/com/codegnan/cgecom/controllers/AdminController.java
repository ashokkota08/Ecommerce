package com.codegnan.cgecom.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session) {
      
        String role = (String) session.getAttribute("role");
        if ("ADMIN".equals(role)) {
            return "admin-dashboard"; 
        } else {
            return "redirect:/login"; 
        }
    }
}

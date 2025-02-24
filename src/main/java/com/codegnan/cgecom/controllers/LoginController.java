package com.codegnan.cgecom.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codegnan.cgecom.model.User;
import com.codegnan.cgecom.service.iface.UserService;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    
  

   
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; 
    }

    
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        
        User user = userService.authenticate(username, password);
        if (user != null) {
           
            session.setAttribute("loggedInUser", user);
            session.setAttribute("role", user.getRole());
            
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard"; 
            } else {
            	 return "redirect:/products"; 
            }
            
            
           
        } else {
            
            model.addAttribute("error", "Invalid credentials");
            return "login"; 
        }
    }

  
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login"; 
    }
}

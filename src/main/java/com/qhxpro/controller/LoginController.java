package com.qhxpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String remember,
            Model model) {

        if ("admin".equals(username) && "password123".equals(password)) {
            model.addAttribute("message", "Login successful! Welcome " + username);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "QHX Pro Dashboard");
        return "dashboard";
    }
}

package com.example.eis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ServiceController {
    @GetMapping("/")
    public String getHome(HttpServletRequest httpServletRequest, Model model) {
        String userId = (String) httpServletRequest.getAttribute("userId");
        model.addAttribute("userId", userId);

        return "index";
    }

    @GetMapping("/images")
    public String getImages(HttpServletRequest httpServletRequest, Model model) {
        String userId = (String) httpServletRequest.getAttribute("userId");
        model.addAttribute("userId", userId);

        return "images";
    }

    @GetMapping("/about")
    public String getAboutUs(HttpServletRequest httpServletRequest, Model model) {

        return "aboutus";
    }
}

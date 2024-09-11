package com.example.eis.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ServiceController {

    @Value("${emofy.filter.service.url}")
    private String filterServiceUrl;

    @Value("${emofy.ml_model.service.url}")
    private String mlModelServiceUrl;


    @GetMapping("/")
    public String getHome(HttpServletRequest httpServletRequest, Model model) {
        String userId = (String) httpServletRequest.getAttribute("userId");
        model.addAttribute("userId", userId);
        model.addAttribute("filterServiceUrl", filterServiceUrl);
        model.addAttribute("mlModelServiceUrl", mlModelServiceUrl);

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

    @GetMapping("/help")
    public String getHelp(HttpServletRequest httpServletRequest, Model model) {

        return "help";
    }
}

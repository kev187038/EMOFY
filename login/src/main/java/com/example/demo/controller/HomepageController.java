package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String getHome() {
        return "loginForm";
    }

    @GetMapping("/serviceTerms")
    public String getTerms() {
        return "serviceTerms";
    }
}

package com.example.eis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ServiceController {
    @GetMapping("/")
    public String getHome() {
        return "home";
    }
}

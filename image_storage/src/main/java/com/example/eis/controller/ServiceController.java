package com.example.eis.controller;

import com.example.eis.utils.KubernetesServiceInfo;

import io.kubernetes.client.openapi.ApiException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ServiceController {

    private final KubernetesServiceInfo serviceInfo;

    public ServiceController() {
        // Inizializza la classe per risolvere l'IP e la porta dei servizi
        this.serviceInfo = new KubernetesServiceInfo();
    }

    @GetMapping("/")
    public String getHome(HttpServletRequest httpServletRequest, Model model) {
        String userId = (String) httpServletRequest.getAttribute("userId");
        model.addAttribute("userId", userId);

        // Risolvi gli URL dei servizi
        String detectEmotionServiceUrl = "";
        String applyFilterServiceUrl = "";

        try {
            detectEmotionServiceUrl = serviceInfo.getServiceUrl("emotion-detector-service", "default");
            applyFilterServiceUrl = serviceInfo.getServiceUrl("image-filters-service", "default");
        } catch (ApiException e) {
            // Gestisci l'eccezione in caso di errore durante la risoluzione degli URL
            e.printStackTrace();
        }
        System.out.println(detectEmotionServiceUrl + "\n"+ applyFilterServiceUrl);

        model.addAttribute("detectFaceServiceUrl", "http://" + detectEmotionServiceUrl + "/detect_face");
        model.addAttribute("detectEmotionServiceUrl", "http://" + detectEmotionServiceUrl + "/detect_emotion");
        model.addAttribute("applyFilterServiceUrl", "http://" + applyFilterServiceUrl + "/apply_filter");

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

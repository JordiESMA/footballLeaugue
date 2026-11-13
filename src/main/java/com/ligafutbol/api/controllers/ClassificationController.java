package com.ligafutbol.api.controllers;

import com.ligafutbol.api.services.ClassificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @GetMapping("/classification")
    public String classification(Model model) {
        model.addAttribute("classification", classificationService.getClassification());
        return "classification";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("classification", classificationService.getClassification());
        return "statistics";
    }
}

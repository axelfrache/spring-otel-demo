package me.axelfrache.springoteldemo.controller;

import me.axelfrache.springoteldemo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final DemoService demoService;

    public HomeController(DemoService demoService) { this.demoService = demoService; }

    @GetMapping("/")
    public String home(Model model) {
        log.info("Appel de / depuis HomeController");
        String result = demoService.doWork();
        model.addAttribute("result", result);
        return "home";
    }

    @PostMapping("/trace")
    public String triggerTrace(RedirectAttributes ra) {
        demoService.generateTraceFromButton();
        ra.addFlashAttribute("message", "Trace générée !");
        return "redirect:/";
    }

}

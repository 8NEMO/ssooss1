package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import searchengine.services.StatisticsService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("statistics", statisticsService.getStatistics());
        return "dashboard";
    }
}
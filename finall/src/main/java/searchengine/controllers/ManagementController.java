package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import searchengine.services.IndexingService;

@Controller
@RequiredArgsConstructor
public class ManagementController {

    private final IndexingService indexingService;

    @PostMapping("/start-indexing")
    public String startIndexing(Model model) {
        indexingService.startIndexing();
        model.addAttribute("message", "Индексация запущена");
        return "management";
    }

    @PostMapping("/stop-indexing")
    public String stopIndexing(Model model) {
        indexingService.stopIndexing();
        model.addAttribute("message", "Индексация остановлена");
        return "management";
    }
}
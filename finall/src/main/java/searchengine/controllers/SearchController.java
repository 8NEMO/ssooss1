package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import searchengine.services.SearchService;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam(name = "query") String query,
                         @RequestParam(name = "site", required = false) String site,
                         Model model) {
        model.addAttribute("results", searchService.search(query, site));
        return "search";
    }
}
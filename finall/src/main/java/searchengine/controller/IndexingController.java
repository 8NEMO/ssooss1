package searchengine.controller;

import org.springframework.web.bind.annotation.*;
import searchengine.services.IndexService;
import searchengine.services.LemmatizationService;
import searchengine.services.SearchResult;

import java.util.List;

@RestController
public class IndexingController {

    private final IndexService indexService;
    private final LemmatizationService lemmatizationService;

    public IndexingController(IndexService indexService, LemmatizationService lemmatizationService) {
        this.indexService = indexService;
        this.lemmatizationService = lemmatizationService;
    }

    @PostMapping("/index-page")
    public String indexPage(@RequestParam String content, @RequestParam String uri, @RequestParam String title) {
        try {
            String cleanedContent = lemmatizationService.cleanHtml(content);
            indexService.addDocument(cleanedContent, uri, title);
            return "Page indexed.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error indexing page.";
        }
    }

    @GetMapping("/search")
    public List<SearchResult> search(@RequestParam String query) {
        try {
            return indexService.search(query);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Return an empty list on error
        }
    }
}

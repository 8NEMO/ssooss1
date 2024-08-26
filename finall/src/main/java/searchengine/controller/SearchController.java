package searchengine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.services.LemmatizationService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final LemmatizationService lemmatizationService;

    public SearchController(LemmatizationService lemmatizationService) {
        this.lemmatizationService = lemmatizationService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Integer>> search(@RequestParam(required = false, defaultValue = "") String query) {
        if (query.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Map<String, Integer> result = lemmatizationService.getLemmas(query);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            // Логирование ошибки и возвращение ответа с ошибкой
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
package searchengine.services;

import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    public List<SearchResult> search(String query, String site) {
        // Логика поиска по сайтам
        List<SearchResult> results = new ArrayList<>();
        // Пример результата поиска
        SearchResult result = new SearchResult();
        result.setTitle("Пример заголовка");
        result.setUrl("https://example.com");
        result.setSnippet("Пример сниппета, содержащего запрос: " + query);
        results.add(result);

        if (site != null && !site.isEmpty()) {
            // Логика фильтрации по конкретному сайту
            result.setUrl(site);
        }

        return results;
    }
}
package searchengine.dto.search;

import lombok.Data;

@Data
public class SearchResult {
    private String title;
    private String url;
    private String snippet;
}
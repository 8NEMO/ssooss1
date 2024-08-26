package searchengine.services;

import java.util.Map;

public class PageResult {

    private final String uri;
    private final String title;
    private final String content;
    private final Map<String, Integer> lemmas;

    public PageResult(String uri, String title, String content, Map<String, Integer> lemmas) {
        this.uri = uri;
        this.title = title;
        this.content = content;
        this.lemmas = lemmas;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Integer> getLemmas() {
        return lemmas;
    }

    public double getAbsoluteRelevance() {
        return lemmas.values().stream().mapToDouble(Integer::doubleValue).sum();
    }
}
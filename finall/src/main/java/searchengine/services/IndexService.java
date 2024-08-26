package searchengine.services;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.stereotype.Service;
import searchengine.services.PageResult;
import searchengine.services.SearchResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexService {

    private final Directory directory = new RAMDirectory();
    private final IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
    private final IndexWriter writer;

    public IndexService() throws Exception {
        writer = new IndexWriter(directory, config);
    }

    public void addDocument(String content, String uri, String title) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("content", content, Field.Store.YES));
        doc.add(new TextField("uri", uri, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        writer.addDocument(doc);
        writer.commit();
    }

    public List<SearchResult> search(String queryStr) throws Exception {
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            Query query = parser.parse(queryStr);

            ScoreDoc[] hits = searcher.search(query, 100).scoreDocs;
            Map<String, PageResult> pageResults = new HashMap<>();

            for (ScoreDoc hit : hits) {
                Document hitDoc = searcher.doc(hit.doc);
                String uri = hitDoc.get("uri");
                String title = hitDoc.get("title");
                String content = hitDoc.get("content");

                if (!pageResults.containsKey(uri)) {
                    pageResults.put(uri, new PageResult(uri, title, content, new HashMap<>()));
                }

                PageResult pageResult = pageResults.get(uri);
                // This is a placeholder for actual rank data retrieval
                pageResult.getLemmas().put(queryStr, pageResult.getLemmas().getOrDefault(queryStr, 0) + 1);
            }

            // Calculate relevance and create results list
            List<SearchResult> results = new ArrayList<>();
            double maxRelevance = pageResults.values().stream()
                    .mapToDouble(PageResult::getAbsoluteRelevance)
                    .max()
                    .orElse(1.0);

            for (PageResult pageResult : pageResults.values()) {
                double absoluteRelevance = pageResult.getAbsoluteRelevance();
                double relativeRelevance = absoluteRelevance / maxRelevance;

                String snippet = createSnippet(pageResult.getContent(), queryStr);

                results.add(new SearchResult(pageResult.getUri(), pageResult.getTitle(), snippet, relativeRelevance));
            }

            return results.stream()
                    .sorted(Comparator.comparingDouble(SearchResult::getRelevance).reversed())
                    .collect(Collectors.toList());
        }
    }

    private String createSnippet(String content, String query) {
        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        String[] parts = lowerContent.split("(?<=\\s|\\b)");

        StringBuilder snippet = new StringBuilder();
        int count = 0;

        for (String part : parts) {
            if (part.contains(lowerQuery)) {
                snippet.append("<b>").append(part).append("</b>");
            } else {
                snippet.append(part);
            }
            count++;
            if (count > 100) break; // Limit snippet length
        }

        return snippet.toString();
    }
}
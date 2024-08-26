package searchengine.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class LemmatizationService {

    private final Analyzer analyzer = new RussianAnalyzer();

    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmaCount = new HashMap<>();
        try (TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text))) {
            CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String lemma = charTermAttr.toString();
                lemmaCount.put(lemma, lemmaCount.getOrDefault(lemma, 0) + 1);
            }
            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lemmaCount;
    }

    public String cleanHtml(String html) {
        return html.replaceAll("<[^>]+>", "");
    }
}
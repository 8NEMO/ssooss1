package searchengine.services;

import org.springframework.stereotype.Service;

@Service
public class IndexingService {

    private boolean indexing = false;

    public synchronized void startIndexing() {
        if (!indexing) {
            indexing = true;
            // Логика запуска индексации
            System.out.println("Индексация запущена");
        }
    }

    public synchronized void stopIndexing() {
        if (indexing) {
            indexing = false;
            // Логика остановки индексации
            System.out.println("Индексация остановлена");
        }
    }

    public synchronized boolean isIndexing() {
        return indexing;
    }
}
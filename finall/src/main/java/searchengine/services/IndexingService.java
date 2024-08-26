package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.model.Site;
import java.util.List;
import java.time.LocalDateTime;
import searchengine.model.SiteStatus;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Service
public class IndexingService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    @Value("${user-agent}")
    private String userAgent;

    @Value("${referrer}")
    private String referrer;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public IndexingService(SiteRepository siteRepository, PageRepository pageRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
    }

    @Transactional
    public void startIndexing() {
        // Получение списка сайтов из конфигурации и запуск индексации
        List<Site> sites = siteRepository.findAll();
        for (Site site : sites) {
            executorService.submit(() -> indexSite(site));
        }
    }

    private void indexSite(Site site) {
        site.setStatus(SiteStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);

        try {
            Set<String> visited = new HashSet<>();
            visitPage(site, site.getUrl(), visited);
            site.setStatus(SiteStatus.INDEXED);
        } catch (Exception e) {
            site.setStatus(SiteStatus.FAILED);
            site.setLastError(e.getMessage());
        } finally {
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
        }
    }

    private void visitPage(Site site, String url, Set<String> visited) throws IOException {
        if (visited.contains(url)) {
            return;
        }
        visited.add(url);

        Document document = Jsoup.connect(url)
                .userAgent(userAgent)
                .referrer(referrer)
                .get();

        // Сохранение страницы в базе данных
        Page page = new Page();
        page.setSite(site);
        page.setPath(url);
        page.setCode(document.connection().response().statusCode());
        page.setContent(document.outerHtml());
        pageRepository.save(page);

        // Рекурсивный обход ссылок
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            if (!visited.contains(nextUrl)) {
                visitPage(site, nextUrl, visited);
            }
        }
    }

    @Transactional
    public void stopIndexing() {
        // Логика остановки индексации (нужно сохранить состояние FAILED и ошибку для текущих задач)
        siteRepository.findAll().forEach(site -> {
            if (site.getStatus() == SiteStatus.INDEXING) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация остановлена пользователем");
                siteRepository.save(site);
            }
        });
    }
}
package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "indexing-settings")
@Primary
public class SitesList {
    private List<Site> sites;
}
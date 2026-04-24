package com.pomguard.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class MavenCentralClient {

    private static final String SEARCH_URL = "https://search.maven.org/solrsearch/select";

    private final RestTemplate restTemplate;

    public MavenCentralClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<String> fetchLatestVersion(String groupId, String artifactId) {
        String query = String.format("g:\"%s\" AND a:\"%s\"", groupId, artifactId);
        String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                .queryParam("q", query)
                .queryParam("rows", 1)
                .queryParam("wt", "json")
                .build()
                .toUriString();
        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null) return Optional.empty();
            JsonNode docs = response.path("response").path("docs");
            if (!docs.isArray() || docs.isEmpty()) return Optional.empty();
            String latest = docs.get(0).path("latestVersion").asText(null);
            return Optional.ofNullable(latest);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

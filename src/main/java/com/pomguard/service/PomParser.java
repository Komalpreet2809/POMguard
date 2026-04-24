package com.pomguard.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.pomguard.model.Dependency;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PomParser {

    private final XmlMapper xmlMapper = new XmlMapper();

    public List<Dependency> parse(InputStream input) throws IOException {
        PomFile pom = xmlMapper.readValue(input, PomFile.class);
        if (pom.dependencies == null) {
            return List.of();
        }
        return pom.dependencies.stream()
                .map(d -> new Dependency(d.groupId, d.artifactId, d.version))
                .toList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PomFile {
        @JacksonXmlElementWrapper(localName = "dependencies")
        @JacksonXmlProperty(localName = "dependency")
        public List<DepXml> dependencies;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DepXml {
        public String groupId;
        public String artifactId;
        public String version;
    }
}

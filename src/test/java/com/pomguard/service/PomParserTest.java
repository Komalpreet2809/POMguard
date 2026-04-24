package com.pomguard.service;

import com.pomguard.model.Dependency;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PomParserTest {

    private final PomParser parser = new PomParser();

    @Test
    void parsesSimplePom() throws Exception {
        String xml = """
            <project>
                <dependencies>
                    <dependency>
                        <groupId>org.json</groupId>
                        <artifactId>json</artifactId>
                        <version>20230227</version>
                    </dependency>
                    <dependency>
                        <groupId>junit</groupId>
                        <artifactId>junit</artifactId>
                        <version>4.13.2</version>
                    </dependency>
                </dependencies>
            </project>
            """;
        List<Dependency> deps = parser.parse(new ByteArrayInputStream(xml.getBytes()));
        assertEquals(2, deps.size());
        assertEquals("org.json", deps.get(0).groupId());
        assertEquals("json", deps.get(0).artifactId());
        assertEquals("20230227", deps.get(0).version());
        assertEquals("junit", deps.get(1).groupId());
    }

    @Test
    void returnsEmptyWhenNoDependencies() throws Exception {
        String xml = "<project></project>";
        List<Dependency> deps = parser.parse(new ByteArrayInputStream(xml.getBytes()));
        assertTrue(deps.isEmpty());
    }
}

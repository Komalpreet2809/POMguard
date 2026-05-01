package com.pomguard.service;

import com.pomguard.model.AuditResult;
import com.pomguard.model.Dependency;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditService {

    private final MavenCentralClient mavenCentralClient;
    private final VersionComparator versionComparator;

    public AuditService(MavenCentralClient mavenCentralClient, VersionComparator versionComparator) {
        this.mavenCentralClient = mavenCentralClient;
        this.versionComparator = versionComparator;
    }

    public List<AuditResult> audit(List<Dependency> dependencies) {
        return dependencies.parallelStream().map(this::auditOne).toList();
    }

    private AuditResult auditOne(Dependency dep) {
        if (dep.version() == null || dep.version().isBlank()) {
            return new AuditResult(dep.groupId(), dep.artifactId(), "(none)", "-",
                    AuditResult.Status.UNKNOWN, "No version declared");
        }
        Optional<String> latest = mavenCentralClient.fetchLatestVersion(dep.groupId(), dep.artifactId());
        if (latest.isEmpty()) {
            return new AuditResult(dep.groupId(), dep.artifactId(), dep.version(), "-",
                    AuditResult.Status.UNKNOWN, "Not found on Maven Central");
        }
        String latestVersion = latest.get();
        if (versionComparator.isOutdated(dep.version(), latestVersion)) {
            return new AuditResult(dep.groupId(), dep.artifactId(), dep.version(), latestVersion,
                    AuditResult.Status.YELLOW, "Newer version available");
        }
        return new AuditResult(dep.groupId(), dep.artifactId(), dep.version(), latestVersion,
                AuditResult.Status.GREEN, "Up to date");
    }
}

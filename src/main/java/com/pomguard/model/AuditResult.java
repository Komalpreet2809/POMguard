package com.pomguard.model;

public record AuditResult(
        String groupId,
        String artifactId,
        String currentVersion,
        String latestVersion,
        Status status,
        String note
) {
    public enum Status { GREEN, YELLOW, RED, UNKNOWN }
}

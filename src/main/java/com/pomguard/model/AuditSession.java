package com.pomguard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record AuditSession(
        String id,
        LocalDateTime timestamp,
        String projectName,
        String findings,
        String statusText,
        String statusClass
) {
    public AuditSession(LocalDateTime timestamp, String projectName, String findings, String statusText, String statusClass) {
        this(UUID.randomUUID().toString(), timestamp, projectName, findings, statusText, statusClass);
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.format(formatter);
    }
}

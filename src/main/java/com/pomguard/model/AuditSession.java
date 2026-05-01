package com.pomguard.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AuditSession(
        LocalDateTime timestamp,
        String projectName,
        String findings,
        String statusText,
        String statusClass
) {
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.format(formatter);
    }
}

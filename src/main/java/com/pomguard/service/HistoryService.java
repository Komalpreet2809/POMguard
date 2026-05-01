package com.pomguard.service;

import com.pomguard.model.AuditResult;
import com.pomguard.model.AuditSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class HistoryService {

    private final List<AuditSession> sessions = new ArrayList<>();

    public void addSession(String filename, List<AuditResult> results) {
        int outdated = 0;
        int critical = 0;
        int unknown = 0;

        for (AuditResult result : results) {
            if (result.status() == AuditResult.Status.YELLOW) {
                outdated++;
            } else if (result.status() == AuditResult.Status.RED) {
                critical++;
            } else if (result.status() == AuditResult.Status.UNKNOWN) {
                unknown++;
            }
        }

        String findings;
        String statusText;
        String statusClass;

        if (critical > 0) {
            findings = critical + " Critical, " + outdated + " Outdated";
            statusText = "AT RISK";
            statusClass = "status-RED";
        } else if (outdated > 0) {
            findings = outdated + " Outdated";
            statusText = "WARNING";
            statusClass = "status-YELLOW";
        } else if (unknown > 0 && results.size() > 0) {
            findings = unknown + " Unknown";
            statusText = "UNKNOWN";
            statusClass = "status-UNKNOWN";
        } else {
            findings = "0 Issues";
            statusText = "SECURE";
            statusClass = "status-GREEN";
        }

        // Add to the front of the list so newest is first
        sessions.add(0, new AuditSession(
                LocalDateTime.now(),
                filename != null ? filename : "pom.xml",
                findings,
                statusText,
                statusClass
        ));

        // Keep only the last 10 sessions to prevent memory leak
        if (sessions.size() > 10) {
            sessions.remove(sessions.size() - 1);
        }
    }

    public void deleteSession(String id) {
        sessions.removeIf(session -> session.id().equals(id));
    }

    public List<AuditSession> getRecentSessions() {
        return Collections.unmodifiableList(sessions);
    }
}

package com.pomguard.service;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.stereotype.Component;

@Component
public class VersionComparator {

    public int compare(String a, String b) {
        return new ComparableVersion(a).compareTo(new ComparableVersion(b));
    }

    public boolean isOutdated(String current, String latest) {
        return compare(current, latest) < 0;
    }
}

package com.pomguard.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionComparatorTest {

    private final VersionComparator vc = new VersionComparator();

    @Test
    void detectsOutdatedVersion() {
        assertTrue(vc.isOutdated("1.2.0", "1.3.0"));
        assertTrue(vc.isOutdated("1.0.0", "2.0.0"));
    }

    @Test
    void currentMatchesLatest() {
        assertFalse(vc.isOutdated("1.2.0", "1.2.0"));
    }

    @Test
    void handlesPreReleaseTags() {
        assertTrue(vc.isOutdated("1.2.1-beta", "1.2.1"));
        assertTrue(vc.isOutdated("1.2.0", "1.2.1-beta"));
    }
}

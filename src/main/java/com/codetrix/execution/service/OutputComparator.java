package com.codetrix.execution.service;

import org.springframework.stereotype.Component;

@Component
public class OutputComparator {

    public boolean compare(String expected, String actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }

        String normalizedExpected = normalize(expected);
        String normalizedActual = normalize(actual);

        return normalizedExpected.equals(normalizedActual);
    }

    public boolean compareStrict(String expected, String actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        return expected.equals(actual);
    }

    private String normalize(String output) {
        if (output == null) {
            return "";
        }

        String[] lines = output.split("\n", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].stripTrailing();
            if (i > 0) {
                result.append("\n");
            }
            result.append(trimmed);
        }

        String normalized = result.toString();
        while (normalized.endsWith("\n")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    public ComparisonResult compareDetailed(String expected, String actual) {
        boolean matches = compare(expected, actual);

        return ComparisonResult.builder()
            .matches(matches)
            .expectedLength(expected != null ? expected.length() : 0)
            .actualLength(actual != null ? actual.length() : 0)
            .expectedLines(expected != null ? expected.split("\n").length : 0)
            .actualLines(actual != null ? actual.split("\n").length : 0)
            .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class ComparisonResult {
        private boolean matches;
        private int expectedLength;
        private int actualLength;
        private int expectedLines;
        private int actualLines;
    }
}

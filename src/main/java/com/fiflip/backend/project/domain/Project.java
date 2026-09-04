package com.fiflip.backend.project.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record Project(
        Long id,
        String title,
        String description,
        ProjectCategory category,
        String coverImageUrl,
        List<String> beforeImageUrls,
        List<String> afterImageUrls,
        ProjectStatus status,
        Double tea,
        Boolean teaProjected,
        LocalDate projectDate,
        Instant createdAt
) {
    public static Project create(
            String title,
            String description,
            ProjectCategory category,
            String coverImageUrl,
            List<String> beforeImageUrls,
            List<String> afterImageUrls,
            ProjectStatus status,
            Double tea,
            Boolean teaProjected,
            LocalDate projectDate
    ) {
        return new Project(null, title, description, category, coverImageUrl, beforeImageUrls, afterImageUrls,
                status, tea, teaProjected, projectDate, Instant.now());
    }

    // Param list deliberately excludes id/createdAt so an update can never touch either —
    // structurally enforced instead of merely remembered.
    public Project withUpdatedFields(
            String title,
            String description,
            ProjectCategory category,
            String coverImageUrl,
            List<String> beforeImageUrls,
            List<String> afterImageUrls,
            ProjectStatus status,
            Double tea,
            Boolean teaProjected,
            LocalDate projectDate
    ) {
        return new Project(id, title, description, category, coverImageUrl, beforeImageUrls, afterImageUrls,
                status, tea, teaProjected, projectDate, createdAt);
    }
}

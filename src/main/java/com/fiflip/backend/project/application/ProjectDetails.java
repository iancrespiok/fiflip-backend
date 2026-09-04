package com.fiflip.backend.project.application;

import com.fiflip.backend.project.domain.ProjectCategory;
import com.fiflip.backend.project.domain.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

public record ProjectDetails(
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
}

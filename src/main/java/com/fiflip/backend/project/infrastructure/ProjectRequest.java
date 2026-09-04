package com.fiflip.backend.project.infrastructure;

import com.fiflip.backend.project.application.ProjectDetails;
import com.fiflip.backend.project.domain.ProjectCategory;
import com.fiflip.backend.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.YearMonth;
import java.util.List;

public record ProjectRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @NotBlank String coverImageUrl,
        @NotEmpty List<String> beforeImageUrls,
        @NotEmpty List<String> afterImageUrls,
        String status,
        Double tea,
        Boolean teaProjected,
        @NotBlank String projectDate) {

    public ProjectDetails toCommand() {
        return new ProjectDetails(
                title,
                description,
                ProjectCategory.valueOf(category),
                coverImageUrl,
                beforeImageUrls,
                afterImageUrls,
                status != null ? ProjectStatus.valueOf(status) : null,
                tea,
                teaProjected,
                YearMonth.parse(projectDate).atDay(1));
    }
}

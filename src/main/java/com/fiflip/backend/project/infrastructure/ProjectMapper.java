package com.fiflip.backend.project.infrastructure;

import com.fiflip.backend.project.domain.Project;

final class ProjectMapper {

    private ProjectMapper() {
    }

    static Project toDomain(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getCoverImageUrl(),
                entity.getBeforeImageUrls(),
                entity.getAfterImageUrls(),
                entity.getStatus(),
                entity.getTea(),
                entity.getTeaProjected(),
                entity.getProjectDate(),
                entity.getCreatedAt());
    }

    static ProjectJpaEntity toEntity(Project project) {
        return new ProjectJpaEntity(
                project.id(),
                project.title(),
                project.description(),
                project.category(),
                project.coverImageUrl(),
                project.beforeImageUrls(),
                project.afterImageUrls(),
                project.status(),
                project.tea(),
                project.teaProjected(),
                project.projectDate(),
                project.createdAt());
    }
}

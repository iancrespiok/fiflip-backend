package com.fiflip.backend.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final ProjectRepository repository;

    public AdminProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    public record ProjectRequest(
            @NotBlank String title,
            @NotBlank String description,
            @NotBlank String category,
            @NotBlank String coverImageUrl,
            @NotEmpty List<String> beforeImageUrls,
            @NotEmpty List<String> afterImageUrls,
            String status,
            Double tea,
            Boolean teaProjected) {
    }

    @PostMapping
    public ResponseEntity<Project> create(@Valid @RequestBody ProjectRequest request) {
        Project project = new Project(
                request.title(),
                request.description(),
                ProjectCategory.valueOf(request.category()),
                request.coverImageUrl(),
                request.beforeImageUrls(),
                request.afterImageUrls(),
                request.status() != null ? ProjectStatus.valueOf(request.status()) : null,
                request.tea(),
                request.teaProjected());
        return ResponseEntity.ok(repository.save(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return repository.findById(id)
                .map(project -> {
                    project.setTitle(request.title());
                    project.setDescription(request.description());
                    project.setCategory(ProjectCategory.valueOf(request.category()));
                    project.setCoverImageUrl(request.coverImageUrl());
                    project.setBeforeImageUrls(request.beforeImageUrls());
                    project.setAfterImageUrls(request.afterImageUrls());
                    project.setStatus(request.status() != null ? ProjectStatus.valueOf(request.status()) : null);
                    project.setTea(request.tea());
                    project.setTeaProjected(request.teaProjected());
                    return ResponseEntity.ok(repository.save(project));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

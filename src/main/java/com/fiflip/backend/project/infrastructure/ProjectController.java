package com.fiflip.backend.project.infrastructure;

import com.fiflip.backend.project.application.ProjectUseCases;
import com.fiflip.backend.project.domain.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectUseCases projectUseCases;

    public ProjectController(ProjectUseCases projectUseCases) {
        this.projectUseCases = projectUseCases;
    }

    @GetMapping
    public List<Project> list() {
        return projectUseCases.listProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> get(@PathVariable Long id) {
        return projectUseCases.getProject(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

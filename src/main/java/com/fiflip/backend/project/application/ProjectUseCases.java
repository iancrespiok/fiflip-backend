package com.fiflip.backend.project.application;

import com.fiflip.backend.project.domain.Project;
import com.fiflip.backend.storage.UploadedFile;

import java.util.List;
import java.util.Optional;

public interface ProjectUseCases {

    List<Project> listProjects();

    Optional<Project> getProject(Long id);

    Project createProject(ProjectDetails details);

    Optional<Project> updateProject(Long id, ProjectDetails details);

    boolean deleteProject(Long id);

    String uploadProjectImage(UploadedFile file);
}

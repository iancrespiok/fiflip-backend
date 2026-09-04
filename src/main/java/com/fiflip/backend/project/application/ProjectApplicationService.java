package com.fiflip.backend.project.application;

import com.fiflip.backend.project.domain.Project;
import com.fiflip.backend.storage.ObjectStorage;
import com.fiflip.backend.storage.UploadedFile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectApplicationService implements ProjectUseCases {

    private final ProjectRepository repository;
    private final ObjectStorage objectStorage;

    public ProjectApplicationService(ProjectRepository repository, ObjectStorage objectStorage) {
        this.repository = repository;
        this.objectStorage = objectStorage;
    }

    @Override
    public List<Project> listProjects() {
        return repository.findAllOrderedByProjectDateDesc();
    }

    @Override
    public Optional<Project> getProject(Long id) {
        return repository.findById(id);
    }

    @Override
    public Project createProject(ProjectDetails details) {
        Project project = Project.create(
                details.title(), details.description(), details.category(), details.coverImageUrl(),
                details.beforeImageUrls(), details.afterImageUrls(), details.status(), details.tea(),
                details.teaProjected(), details.projectDate());
        return repository.save(project);
    }

    @Override
    public Optional<Project> updateProject(Long id, ProjectDetails details) {
        return repository.findById(id)
                .map(existing -> existing.withUpdatedFields(
                        details.title(), details.description(), details.category(), details.coverImageUrl(),
                        details.beforeImageUrls(), details.afterImageUrls(), details.status(), details.tea(),
                        details.teaProjected(), details.projectDate()))
                .map(repository::save);
    }

    @Override
    public boolean deleteProject(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    public String uploadProjectImage(UploadedFile file) {
        return objectStorage.upload(file, "projects");
    }
}

package com.fiflip.backend.project.application;

import com.fiflip.backend.project.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    List<Project> findAllOrderedByProjectDateDesc();

    Optional<Project> findById(Long id);

    Project save(Project project);

    boolean existsById(Long id);

    void deleteById(Long id);
}

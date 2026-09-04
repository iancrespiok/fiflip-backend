package com.fiflip.backend.project.infrastructure;

import com.fiflip.backend.project.application.ProjectRepository;
import com.fiflip.backend.project.domain.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaProjectRepository implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    public JpaProjectRepository(ProjectJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Project> findAllOrderedByProjectDateDesc() {
        return jpaRepository.findAllByOrderByProjectDateDesc().stream()
                .map(ProjectMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpaRepository.findById(id).map(ProjectMapper::toDomain);
    }

    @Override
    public Project save(Project project) {
        ProjectJpaEntity saved = jpaRepository.save(ProjectMapper.toEntity(project));
        return ProjectMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}

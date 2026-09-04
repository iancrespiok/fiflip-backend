package com.fiflip.backend.project.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, Long> {
    List<ProjectJpaEntity> findAllByOrderByProjectDateDesc();
}

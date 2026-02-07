package br.com.engepro.api.repository

import br.com.engepro.api.model.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByStepId(Long stepId)
}

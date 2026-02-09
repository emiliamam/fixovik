package ru.rutmiit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.ProjectTask;
import ru.rutmiit.models.enums.Status;

import java.util.List;
import java.util.Optional;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, String> {

    List<ProjectTask> findAllByProjectId(String projectId);
//    List<ProjectTask> findByProjectId(String projectId);
}

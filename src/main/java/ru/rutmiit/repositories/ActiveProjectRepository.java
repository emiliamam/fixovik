package ru.rutmiit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.ProjectTask;
import ru.rutmiit.models.enums.Status;

import java.util.List;

@Repository
public interface ActiveProjectRepository extends JpaRepository<Project, String> {
    List<Project> findByBrigadierId(String brigadierId);
    List<Project> findByBrigadier_IdAndStatus(String brigadierId, Status status);

    List<Project> findByMasters_Id(String masterId);
}

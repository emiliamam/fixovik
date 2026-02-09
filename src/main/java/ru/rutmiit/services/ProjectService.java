package ru.rutmiit.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.rutmiit.dto.AddProjectDto;
import ru.rutmiit.dto.ShowProjectInfoDto;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;
import java.time.LocalDate;
import ru.rutmiit.models.enums.Status;

import java.util.List;

public interface ProjectService {

    void projectAdd(AddProjectDto projectDto);

    void projectAddMasters(List<Master> masters);

    public void updateProject(String projectName, AddProjectDto projectDto);

    List<ShowProjectInfoDto> allProject();

    Page<ShowProjectInfoDto> allProjectPaginated(Pageable pageable);

    List<ShowProjectInfoDto> searchProject(String searchTerm);

    ShowProjectInfoDto editProject(String projectName);

    public Project findById(String id);

    Project findByName(String name);

    void deleteProject(String name);

    public long countAllProjects();

    public long countAllActiveProjects();

    Page<ShowProjectInfoDto> findProjectsWithFilter(Status status,
                                                    LocalDate startDateFrom,
                                                    LocalDate startDateTo,
                                                    Pageable pageable);


}

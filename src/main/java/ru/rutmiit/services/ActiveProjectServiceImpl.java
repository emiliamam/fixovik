package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.ShowActiveProjectDto;
import ru.rutmiit.dto.ShowProjectTask;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.ProjectTask;
import ru.rutmiit.models.exceptions.CompanyNotFoundException;
import ru.rutmiit.models.exceptions.TaskException;
import ru.rutmiit.repositories.ActiveProjectRepository;
import ru.rutmiit.repositories.ProjectTaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ActiveProjectServiceImpl implements ActiveProjectService{

    private final ActiveProjectRepository activeProjectRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ModelMapper mapper;

    public ActiveProjectServiceImpl(ActiveProjectRepository activeProjectRepository, ProjectTaskRepository projectTaskRepository, ModelMapper mapper) {
        this.activeProjectRepository = activeProjectRepository;
        this.projectTaskRepository = projectTaskRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ShowActiveProjectDto> allCompanies(String brigadierId) {
        log.debug("Получение активных проектов для бригадира: {}", brigadierId);

        return activeProjectRepository.findByBrigadierId(brigadierId).stream()
                .map(project -> mapper.map(project, ShowActiveProjectDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowProjectTask> projectTask(String projectId) {
        log.debug("Получение задач по проекту: {}", projectId);

        List<ProjectTask> tasks = projectTaskRepository.findAllByProjectId(projectId);

        if (tasks.isEmpty()) {
            log.warn("Для проекта {} не найдено задач", projectId);
            throw new TaskException("Для проекта нет задач");
        }

        return tasks.stream()
                .map(t -> mapper.map(t, ShowProjectTask.class))
                .toList();
    }

    @Override
    public List<ShowActiveProjectDto> allCompaniesForMaster(String masterId) {
        log.debug("Получение активных проектов для мастера: {}", masterId);

        return activeProjectRepository.findByMasters_Id(masterId).stream()
                .map(project -> mapper.map(project, ShowActiveProjectDto.class))
                .toList();
    }
}

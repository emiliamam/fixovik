package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddProjectDto;
import ru.rutmiit.dto.ShowProjectInfoDto;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.ProjectTask;
import ru.rutmiit.models.enums.Status;
import ru.rutmiit.models.exceptions.BrigadierException;
import ru.rutmiit.models.exceptions.ProjectNotFoundException;
import ru.rutmiit.repositories.*;
import ru.rutmiit.repositories.ActiveProjectRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final MasterRepository masterRepository;
    private final ActiveProjectRepository activeProjectRepository;
    private final ModelMapper mapper;
    private final CurrentUserService currentUserService;
    private final ProjectTaskRepository projectTaskRepository;
    public ProjectServiceImpl(ProjectRepository projectRepository, MasterRepository masterRepository, ActiveProjectRepository projectTaskRepository, ModelMapper mapper, CurrentUserService currentUserService, ProjectTaskRepository projectTaskRepository1) {
        this.projectRepository = projectRepository;
        this.masterRepository = masterRepository;
        this.activeProjectRepository = projectTaskRepository;
        this.mapper = mapper;
        this.currentUserService = currentUserService;
        this.projectTaskRepository = projectTaskRepository1;
        log.info("ProjectServiceImpl инициализирован");
    }


    @Override
    @Transactional
    @CacheEvict(cacheNames = "projects", allEntries = true)
    public void projectAdd(AddProjectDto projectDto) {
        log.info("Добавление нового проекта: {}", projectDto.getName());
        Project project = new Project();
        project.setName(projectDto.getName());
        project.setAddress(projectDto.getAddress());
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setSalary(projectDto.getSalary());
        project.setSpace(projectDto.getSpace());
        project.setClient(projectDto.getClientFio());
        project.setPhoneClient(projectDto.getPhoneClient());
        project.setEmail(projectDto.getEmail());
        project.setType(projectDto.getType());
        project.setStatus(projectDto.getStatus());

        if (projectDto.getBrigadierId() != null && !projectDto.getBrigadierId().isBlank()) {

            if (projectDto.getStatus() == Status.InProgress
                    && projectRepository.existsByBrigadierIdAndStatus(projectDto.getBrigadierId(), Status.InProgress)) {
                throw new BrigadierException("У выбранного бригадира уже есть проект в процессе");
            }

            Master brigadier = masterRepository.findById(projectDto.getBrigadierId())
                    .orElseThrow(() -> new RuntimeException("Бригадир не найден"));

            project.setBrigadier(brigadier);
        }

        if (projectDto.getMasterIds() != null && !projectDto.getMasterIds().isEmpty()) {
            for (String masterId : projectDto.getMasterIds()) {
                Master master = masterRepository.findById(masterId)
                        .orElseThrow(() -> new RuntimeException("Мастер не найден: " + masterId));
                master.setProject(project);
            }
        }
       List<ProjectTask> tasksList = List.of( createTask("Демонтаж старой отделки", project),
               createTask("Разводка электрики", project),
               createTask("Штукатурка стен", project),
               createTask("Чистовая отделка", project));
       projectRepository.save(project);
       projectTaskRepository.saveAll(tasksList);
       log.info("Проект успешно добавлен");
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "projects", allEntries = true)
    public void updateProject(String projectName, AddProjectDto projectDto){
        Project project = projectRepository.findByName(projectName)
                .orElseThrow(() -> new RuntimeException("Проект не найден"));

        project.setName(projectDto.getName());
        project.setAddress(projectDto.getAddress());
        project.setDescription(projectDto.getDescription());
        project.setSpace(projectDto.getSpace());
        project.setSalary(projectDto.getSalary());
        project.setPhoneClient(projectDto.getPhoneClient());
        project.setEmail(projectDto.getEmail());
        project.setType(projectDto.getType());
        project.setStatus(projectDto.getStatus());

        if (projectDto.getBrigadierId() != null && !projectDto.getBrigadierId().isEmpty()){
            Master brigadier = masterRepository.findById(projectDto.getBrigadierId())
                    .orElseThrow(() -> new RuntimeException("Бригадир не найден"));
            project.setBrigadier(brigadier);
        } else {
            project.setBrigadier(null);
        }

        Set<String> selectedIds = projectDto.getMasterIds();
        Set<Master> currentMasters = project.getMasters();

        for (var it = currentMasters.iterator(); it.hasNext(); ) {
            Master m = it.next();
            if (!selectedIds.contains(m.getId())) {
                m.setProject(null);
                it.remove();
            }
        }
        for (String id : selectedIds) {
            boolean already = currentMasters.stream()
                    .anyMatch(m -> m.getId().equals(id));
            if (!already) {
                Master m = masterRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Мастер не найден"));
                m.setProject(project);

                currentMasters.add(m);

            }
        }

        projectRepository.save(project);
        log.info("Проект успешно обновлен");
    }


    @Override
    public void projectAddMasters(List<Master> masters) {

    }
    @Override
    @Cacheable(value = "projects", key = "'all'")
    public List<ShowProjectInfoDto> allProject() {
        log.debug("Получение списка всех проектов");

        List<ShowProjectInfoDto> projects = projectRepository.findAll().stream()
                .map(project -> {
                    ShowProjectInfoDto dto = mapper.map(project, ShowProjectInfoDto.class);
                    if(project.getBrigadier() != null) {
                        String brigadierFio = project.getBrigadier().getFio();
                        dto.setBrigadier(brigadierFio);
                        dto.setBrigadierId(project.getBrigadier().getId());
                    } else {
                        dto.setBrigadier(null);
                        dto.setBrigadierId(null);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        log.info("Найдено проектов: {}", projects.size());
        return projects;
    }
    @Override
    public Page<ShowProjectInfoDto> allProjectPaginated(Pageable pageable) {
        log.debug("Получение компаний с пагинацией: страница {}, размер {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return projectRepository.findAll(pageable)
                .map(company -> mapper.map(company, ShowProjectInfoDto.class));
    }

    @Override
    public List<ShowProjectInfoDto> searchProject(String searchTerm) {
        List<ShowProjectInfoDto> showProjectInfoDtos2 = projectRepository.searchByNameOrDescriptionOrAddress(searchTerm).stream()
                .map(company -> mapper.map(company, ShowProjectInfoDto.class))
                .collect(Collectors.toList());
        return showProjectInfoDtos2;
    }

    @Override
    public ShowProjectInfoDto editProject(String projectName) {
        log.debug("Получение деталей компании: {}", projectName);
        Project project = projectRepository.findByName(projectName)
                .orElseThrow(() -> {
                    log.warn("Проект не найден: {}", projectName);
                    return new Error("Проект с именем '" + projectName + "' не найден");
                });

        ShowProjectInfoDto dto = mapper.map(project, ShowProjectInfoDto.class);

        if (project.getBrigadier() != null) {
            dto.setBrigadier(project.getBrigadier().getFio());
            dto.setBrigadierId(project.getBrigadier().getId());
        } else {
            dto.setBrigadier(null);
            dto.setBrigadierId(null);
        }

        dto.setMasterIds(
                project.getMasters().stream()
                        .map(Master::getId)
                        .collect(java.util.stream.Collectors.toSet())
        );
        return dto;
    }

    private ProjectTask createTask(String name, Project project){
        ProjectTask projectTask = new ProjectTask();
        projectTask.setName(name);
        projectTask.setProject(project);
        projectTask.setIsCompleted(false);
        return projectTask;
    }


    @Override
    public Project findById(String id) {
        log.debug("Поиск компаний в городе: {}", id);
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Проект не найден: "));

    }

    @Override
    public Project findByName(String name) {
        return projectRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Проект не найден: " + name));
    }

    @Override
    @Transactional
    public void deleteProject(String name) {
        if(!projectRepository.existsByName(name)){
            throw new ProjectNotFoundException("Проект с именем "+ name +" не найден");
        }
        projectRepository.deleteByName(name);
        log.info("Компания успешно удалена: {}", name);
    }

    @Override
    public long countAllProjects() {
        return projectRepository.count();
    }

    @Override
    public long countAllActiveProjects() {
        return projectRepository.countByStatus(Status.InProgress);
    }

    @Override
    public Page<ShowProjectInfoDto> findProjectsWithFilter(Status status,
                                                           LocalDate startDateFrom,
                                                           LocalDate startDateTo,
                                                           Pageable pageable) {

        Page<Project> page;

        if (status != null && startDateFrom != null && startDateTo != null) {
            page = projectRepository
                    .findByStatusAndStartDateBetween(status, startDateFrom, startDateTo, pageable);

        } else if (status != null && startDateFrom != null) {
            page = projectRepository
                    .findByStatusAndStartDateGreaterThanEqual(status, startDateFrom, pageable);

        } else if (status != null && startDateTo != null) {
            page = projectRepository
                    .findByStatusAndStartDateLessThanEqual(status, startDateTo, pageable);

        } else if (status != null) {
            page = projectRepository.findByStatus(status, pageable);

        } else if (startDateFrom != null && startDateTo != null) {
            page = projectRepository
                    .findByStartDateBetween(startDateFrom, startDateTo, pageable);

        } else if (startDateFrom != null) {
            page = projectRepository
                    .findByStartDateGreaterThanEqual(startDateFrom, pageable);

        } else if (startDateTo != null) {
            page = projectRepository
                    .findByStartDateLessThanEqual(startDateTo, pageable);

        } else {
            page = projectRepository.findAll(pageable);
        }

        return page.map(p -> mapper.map(p, ShowProjectInfoDto.class));
    }

}

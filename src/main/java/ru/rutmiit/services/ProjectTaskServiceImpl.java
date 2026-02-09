package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.ProjectTask;
import ru.rutmiit.models.enums.Status;
import ru.rutmiit.models.exceptions.ProjectNotFoundException;
import ru.rutmiit.repositories.ProjectRepository;
import ru.rutmiit.repositories.ProjectTaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectTaskServiceImpl implements ProjectTaskService{

    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository projectTaskRepository;

    public ProjectTaskServiceImpl(ProjectRepository projectRepository, ProjectTaskRepository projectTaskRepository) {
        this.projectRepository = projectRepository;
        this.projectTaskRepository = projectTaskRepository;
    }

    @Override
    @Transactional
    public void updateTasksCompletion(String projectId, List<String> completedTaskIds, Status status) {
        List<ProjectTask> tasks = projectTaskRepository.findAllByProjectId(projectId);
        for( ProjectTask task: tasks){
            boolean isCompleted = completedTaskIds.contains(task.getId());
            task.setIsCompleted(isCompleted);
        }
        projectTaskRepository.saveAll(tasks);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Проект не найден"));
        project.setStatus(status);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void addTasks(Project project, List<String> taskNames) {
        List<ProjectTask> projectTaskList = new ArrayList<>();
        for (String task: taskNames){
            projectTaskList.add(createTask(task, project));
        }
        log.info("projectTask: {}", taskNames);
        log.info("taskNames size = {}", taskNames.size());
        projectTaskRepository.saveAll(projectTaskList);
    }

    @Override
    @Transactional
    public void deleteTask(String id) {
        projectTaskRepository.deleteById(id);
    }

    public ProjectTask createTask(String name, Project project){
        ProjectTask projectTask = new ProjectTask();
        projectTask.setName(name);
        projectTask.setIsCompleted(false);
        projectTask.setProject(project);
        return projectTask;
    }
}

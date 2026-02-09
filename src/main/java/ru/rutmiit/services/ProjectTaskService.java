package ru.rutmiit.services;

import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.enums.Status;

import java.util.List;

public interface ProjectTaskService {

    void updateTasksCompletion(String projectId, List<String> completedTaskIds, Status status);

    void addTasks(Project project, List<String> taskNames);

    void deleteTask(String id);

}

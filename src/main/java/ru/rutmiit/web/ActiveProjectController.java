package ru.rutmiit.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.*;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.enums.ObjectLevels;
import ru.rutmiit.models.enums.Status;
import ru.rutmiit.models.exceptions.TaskException;
import ru.rutmiit.repositories.MasterRepository;
import ru.rutmiit.repositories.ProjectTaskRepository;
import ru.rutmiit.services.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/active-projects")
public class ActiveProjectController {
    private ActiveProjectService activeProjectService;
    private ProjectService projectService;
    private MasterService masterService;
    private ProjectTaskService projectTaskService;
    private final MasterRepository masterRepository;
    @Autowired
    private CurrentUserService currentUserService;

    public ActiveProjectController(ActiveProjectService activeProjectService, ProjectService projectService, MasterService masterService, ProjectTaskService projectTaskService, MasterRepository masterRepository, CurrentUserService currentUserService) {
        this.activeProjectService = activeProjectService;
        this.projectService = projectService;
        this.masterService = masterService;
        this.projectTaskService = projectTaskService;
        this.masterRepository = masterRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/all")
    public String showAllActiveProjects(Authentication authentication, Model model) {
        try {
            String userName = authentication.getName();

            Master master = masterRepository.findByUser_Username(userName).orElseThrow(() -> new RuntimeException("Мастер для пользователя не найден"));

            List<ShowActiveProjectDto> projects;
            if(Boolean.TRUE.equals(master.getIsBrigadier())){
                projects = activeProjectService.allCompanies(master.getId());
            } else {
                projects = activeProjectService.allCompaniesForMaster(master.getId());
            }

            if (projects.isEmpty()) {
                model.addAttribute("hasProject", false);
                model.addAttribute("message", "У вас нет активных проектов");
            } else {
                model.addAttribute("hasProject", true);
                model.addAttribute("project", projects);
            }

        } catch (Exception e) {
            log.error("Error loading active projects: ", e);
            model.addAttribute("error", "Ошибка при загрузке проектов: " + e.getMessage());
            model.addAttribute("hasProject", false);
        }

        return "active-projects";
    }

    @GetMapping("/project-task/{project-name}")
    public String companyDetails(@PathVariable("project-name") String projectName, Model model) {
        log.debug("Запрос деталей компании: {}", projectName);
        try {
            Project project = projectService.findByName(projectName);
            model.addAttribute("projectName", project.getName());
            model.addAttribute("projectId", project.getId());
            model.addAttribute("projectStatus", project.getStatus());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("projectTask", activeProjectService.projectTask(project.getId()));

            return "project-task";

        } catch (TaskException e){
            log.warn("Нарушение правила занятости бригадира: {}", e.getMessage());
            Project project = projectService.findByName(projectName);
            model.addAttribute("projectTask", activeProjectService.projectTask(project.getId()));
            return "/active-projects";

        }
    }

    @PostMapping("/project-task/save")
    public String updateCheckList(@RequestParam("projectId") String projectId,
                                  @RequestParam(value = "status") Status status,
                                  @RequestParam(value = "completedTaskIds", required = false)
                                      List<String> completedTaskIds,
                                  RedirectAttributes redirectAttributes ){
        if (completedTaskIds == null) {
            completedTaskIds = List.of();
        }

        projectTaskService.updateTasksCompletion(projectId, completedTaskIds, status);

        redirectAttributes.addFlashAttribute("successMessage", "Изменения сохранены");
        return "redirect:/active-projects/all";
    }

    @GetMapping("/project-task/add/{project-name}")
    public String checklistAdd(@PathVariable("project-name") String projectName,Model model) {
//        log.debug("Запрос деталей компании: {}", projectName);
        try {
            Project project = projectService.findByName(projectName);
            model.addAttribute("projectName", project.getName());
            model.addAttribute("projectId", project.getId());
            model.addAttribute("projectStatus", project.getStatus());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("projectTask", activeProjectService.projectTask(project.getId()));

            return "project-task-add";

        } catch (TaskException e){
            log.warn("Нарушение правила занятости бригадира: {}", e.getMessage());
            Project project = projectService.findByName(projectName);
            model.addAttribute("projectTask", activeProjectService.projectTask(project.getId()));
            return "/active-projects";

        }
    }

    @PostMapping("/project-task/add")
    public String addCheckListTask(@RequestParam("projectId") String projectId,
                                   @RequestParam("taskNames") List<String> taskNames,
                                   RedirectAttributes redirectAttributes ){
        Project project = projectService.findById(projectId);

       projectTaskService.addTasks(project, taskNames);

        log.info("Запрос деталей компании: {}", project.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Изменения сохранены");
        redirectAttributes.addAttribute("project-name", project.getName());

        return "redirect:/active-projects/project-task/{project-name}";    }

    @DeleteMapping("/project-task/delete/{id}")
    public String deleteTask(@PathVariable String id,
                             @RequestParam("projectId") String projectId,
                             @RequestParam("projectName") String projectName,
                             RedirectAttributes redirectAttributes) {

        Project project = projectService.findById(projectId);
        projectTaskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("successMessage", "Задача удалена");
        redirectAttributes.addAttribute("project-name", project.getName());

        return "redirect:/active-projects/project-task/{project-name}";     }

}
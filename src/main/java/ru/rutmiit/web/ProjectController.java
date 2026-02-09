package ru.rutmiit.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.AddProjectDto;
import ru.rutmiit.dto.ShowMasterInfoDto;
import ru.rutmiit.dto.ShowProjectInfoDto;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.enums.ObjectLevels;
import ru.rutmiit.models.enums.Status;
import ru.rutmiit.models.exceptions.BrigadierException;
import ru.rutmiit.services.MasterService;
import ru.rutmiit.services.ProjectService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final MasterService masterService;

    public ProjectController(ProjectService projectService, MasterService masterService) {
        this.projectService = projectService;
        this.masterService = masterService;
        log.info("Инициализация ProjectController");
    }

    @GetMapping("/add")
    public String showAddProjectForm(Model model) {
        try {
            log.debug("GET /projects/add - отображение формы добавления проекта");

            if (!model.containsAttribute("projectModel")) {
                AddProjectDto projectDto = new AddProjectDto();
                model.addAttribute("projectModel", projectDto);
                log.debug("Создан новый AddProjectDto: {}", projectDto);
            }
            List<Master> brigadiers = masterService.getBrigadiers();
            List<Master> masters = masterService.getAllMasters();
            model.addAttribute("brigadiers", brigadiers);
            model.addAttribute("masters", masters);

            model.addAttribute("objectLevels", ObjectLevels.values());
            model.addAttribute("statuses", Status.values());

            return "project-add";

        } catch (Exception e) {
            log.error("Ошибка при отображении формы добавления проекта", e);
            model.addAttribute("errorMessage", "Ошибка загрузки формы: " + e.getMessage());
            return "custom-error";
        }
    }

    @PostMapping("/add")
    public String addProject(@Valid @ModelAttribute("projectModel") AddProjectDto projectModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        log.debug("POST /projects/add - получены данные: {}", projectModel);
        model.addAttribute("ObjectLevels", ObjectLevels.values());
        model.addAttribute("statuses", Status.values());

        try {
            if (bindingResult.hasErrors()) {
                log.warn("Ошибки валидации: {}", bindingResult.getAllErrors());
                List<Master> brigadirs = masterService.getAllMasters();
                List<Master> brigadiers = masterService.getBrigadiers();

                model.addAttribute("brigadirs", brigadirs);
                model.addAttribute("brigadiers", brigadiers);
                model.addAttribute("projectModel", projectModel);
                model.addAttribute("errors", bindingResult.getAllErrors());

                return "project-add";
            }

            log.debug("Валидация пройдена, сохранение проекта...");
            projectService.projectAdd(projectModel);
            List<Master> brigadiers =
                    masterService.getBrigadiers();
            log.debug("POST /projects/add - получены данные по бригадирам: {}", brigadiers);

            model.addAttribute("brigadiers", brigadiers);
            List<Master> brigadirs = masterService.getAllMasters();
            model.addAttribute("brigadirs", brigadirs);

            log.info("Проект успешно добавлен: {}", projectModel.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Проект '" + projectModel.getName() + "' успешно добавлен!");

            return "redirect:/projects/all";

        } catch (BrigadierException e) {
            log.warn("Нарушение правила занятости бригадира: {}", e.getMessage());
            bindingResult.rejectValue("brigadierId", "brigadier.busy", e.getMessage());
            List<Master> brigadirs = masterService.getAllMasters();
            List<Master> brigadiers =
                    masterService.getBrigadiers();
            redirectAttributes.addFlashAttribute("projectModel", projectModel);
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "У выбранного бригадира уже есть проект в процессе. Выберите другого бригадира."
            );
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.projectModel",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("ObjectLevels", ObjectLevels.values());
            redirectAttributes.addFlashAttribute("statuses", Status.values());
            model.addAttribute("brigadirs", brigadirs);
            model.addAttribute("brigadiers", brigadiers);

            redirectAttributes.addFlashAttribute("project", projectModel);
            return "redirect:/projects/add";
        } catch (Exception e) {
            log.error("Критическая ошибка при добавлении проекта", e);

            model.addAttribute("objestLevels", ObjectLevels.values());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("errorMessage",
                    "Ошибка при добавлении проекта: " + e.getMessage() +
                            ". Подробности в логах сервера.");

            return "project-add";
        }
    }


    @GetMapping("/all")
    public String showAllProject(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statusFilter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDateTo,

            Model model) {
        Sort sort = Sort.by(sortBy).descending();

        log.debug("Отображение списка компаний: страница={}, размер={}, сортировка={}, поиск={}",
                page, size, sortBy, search);
        Pageable pageable = PageRequest.of(page, size,sort);

        Page<ShowProjectInfoDto> projectPage;
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("projectInfos", projectService.searchProject(search));
            model.addAttribute("search", search);
        }  else {
            Status status = null;
            if (statusFilter != null && !statusFilter.isBlank()) {
                try {
                    status = Status.valueOf(statusFilter);
                } catch (IllegalArgumentException e) {
                    log.warn("Неизвестный статус фильтра: {}", statusFilter);
                }
            }

            projectPage = projectService.findProjectsWithFilter(status, startDateFrom, startDateTo, pageable);

            model.addAttribute("projectInfos", projectPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", projectPage.getTotalPages());
            model.addAttribute("totalItems", projectPage.getTotalElements());
        }
        model.addAttribute("statuses", Status.values());
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("startDateFrom", startDateFrom);
        model.addAttribute("startDateTo", startDateTo);
        return "project-all";
    }
    @GetMapping("/edit/{project-name}")
    public String editProject(@PathVariable("project-name") String projectName, Model model){
        log.debug("Запрос деталей компании: {}", projectName);
        ShowProjectInfoDto projectDetails = projectService.editProject(projectName);
        List<Master> brigadiers = masterService.getBrigadiers();
        List<Master> masters = masterService.getAllMasters();
        AddProjectDto projectModel = new AddProjectDto();
        projectModel.setName(projectDetails.getName());
        projectModel.setAddress(projectDetails.getAddress());
        projectModel.setDescription(projectDetails.getDescription());
        projectModel.setSalary(projectDetails.getSalary());
        projectModel.setSpace(projectDetails.getSpace());
        projectModel.setPhoneClient(projectDetails.getPhoneClient());
        projectModel.setEmail(projectDetails.getEmail());
        projectModel.setType(projectDetails.getType());
        projectModel.setStatus(projectDetails.getStatus());
        projectModel.setMasterIds(projectDetails.getMasterIds());
        projectModel.setBrigadierId(projectDetails.getBrigadierId());
        projectModel.setStatus(projectDetails.getStatus());
        projectModel.setType(projectDetails.getType());
        model.addAttribute("projectDetails", projectDetails);
        model.addAttribute("projectModel", projectModel);
        model.addAttribute("brigadiers", brigadiers);
        model.addAttribute("masters", masters);
        return "edit";
    }
    @PostMapping("/edit/{project-name}")
    public String updateProject(@PathVariable("project-name") String projectName, @ModelAttribute("projectModel") AddProjectDto projectModel ){
        projectService.updateProject(projectName, projectModel);
        log.info("Проект успешно добавлен: {}", projectModel.getName());
        return "redirect:/projects/all";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") String companyName,
                                RedirectAttributes redirectAttributes) {
        log.debug("Запрос на удаление проекта: {}", companyName);
        projectService.deleteProject(companyName);
        redirectAttributes.addFlashAttribute("successMessage",
                "Проект '" + companyName + "' успешно удален!");
        return "redirect:/projects/all";
    }


}
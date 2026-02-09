package ru.rutmiit.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.services.MasterService;
import ru.rutmiit.services.ProjectService;

/**
 * Контроллер для главной страницы.
 */
@Slf4j
@Controller
public class HomeController {

    private final ProjectService projectService;
    private final MasterService masterService;

    public HomeController(ProjectService projectService, MasterService masterService) {
        this.projectService = projectService;
        this.masterService = masterService;
    }

    @GetMapping("/")
    public String homePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            log.debug("Анонимный пользователь -> index");
            return "index";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRoles.ADMIN.name()));

        boolean isModerator = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + UserRoles.MODERATOR.name()));

        if (isAdmin) {
            Long countProject = projectService.countAllProjects();
            Long countMaster = masterService.countAllMasters();
            Long countProgressProject = projectService.countAllActiveProjects();
            model.addAttribute("totalProjects", countProject);
            model.addAttribute("totalMasters", countMaster);
            model.addAttribute("activeProjects", countProgressProject);

            return "index-admin";
        } else {
            Long countProject = projectService.countAllProjects();
            Long countMaster = masterService.countAllMasters();
            Long countProgressProject = projectService.countAllActiveProjects();
            return "index-master";
        }
    }
}


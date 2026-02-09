package ru.rutmiit.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.AddMasterDto;
import ru.rutmiit.dto.ShowMasterInfoDto;
import ru.rutmiit.services.AuthService;
import ru.rutmiit.services.MasterService;

@Slf4j
@Controller
@RequestMapping("/masters")
public class MasterController {
    private final MasterService masterService;
    private final AuthService authService;

    public MasterController(MasterService masterService, AuthService authService) {
        this.masterService = masterService;
        this.authService = authService;
    }


    @GetMapping("/add")
    public String addMaster() {
        log.debug("Отображение формы добавления компании");
        return "master-add";
    }

    @ModelAttribute("masterModel")
    public AddMasterDto initMaster() {
        return new AddMasterDto();
    }


    @PostMapping("/add")
    public String addMaster(@Valid AddMasterDto masterModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        log.debug("Обработка POST запроса на добавление компании");

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении мастера: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("companyModel", masterModel);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.companyModel",
                    bindingResult);
            return "redirect:/masters/add";
        }

        masterService.addMaster(masterModel);
        redirectAttributes.addFlashAttribute("successMessage",
                "Мастер '" + masterModel.getFio() + "' успешно добавлена!");

        return "redirect:/masters/all";
    }

    @GetMapping("/all")
    public String showAllMasters(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "experience") String sortBy,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(defaultValue = "false") boolean brigadiersOnly,
                                 Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        model.addAttribute("search", search);
        model.addAttribute("brigadiersOnly", brigadiersOnly);
        if (brigadiersOnly) {
            if (search != null && !search.isBlank()) {
                var filtered = masterService.searchMaster(search).stream()
                        .filter(ShowMasterInfoDto::getIsBrigadier)
                        .toList();

                model.addAttribute("masterInfos", filtered);
                return "master-all";
            }
            Page<ShowMasterInfoDto> masterPage = masterService.findBrigadiersPaginated(pageable);
            model.addAttribute("masterInfos", masterPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", masterPage.getTotalPages());
            model.addAttribute("totalItems", masterPage.getTotalElements());
            return "master-all";
        }

        if (search != null && !search.isBlank()) {
            model.addAttribute("masterInfos", masterService.searchMaster(search));
        } else {
            Page<ShowMasterInfoDto> masterPage = masterService.allMasterPaginated(pageable);
            model.addAttribute("masterInfos", masterPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", masterPage.getTotalPages());
            model.addAttribute("totalItems", masterPage.getTotalElements());
        }

        return "master-all";
    }

    @PostMapping("/brigadier/remove/{email}")
    public String removeBrigadier(@PathVariable String email,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "false") boolean brigadiersOnly,
                                  RedirectAttributes redirectAttributes) {

        masterService.setBrigadier(email, false);

        redirectAttributes.addFlashAttribute("successMessage", "Бригадир снят. Теперь это обычный мастер");
        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("brigadiersOnly", brigadiersOnly);

        return "redirect:/masters/all";
    }
    @PostMapping("/brigadier/set/{email}")
    public String setBrigadier(@PathVariable String email,
                               @RequestParam(required = false) String search,
                               @RequestParam(defaultValue = "false") boolean brigadiersOnly,
                               RedirectAttributes redirectAttributes) {

        masterService.setBrigadier(email, true);

        redirectAttributes.addFlashAttribute("successMessage", "Назначен бригадир");
        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("brigadiersOnly", brigadiersOnly);
        return "redirect:/masters/all";
    }
}









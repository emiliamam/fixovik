package ru.rutmiit.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddMasterDto;
import ru.rutmiit.dto.ShowMasterInfoDto;
import ru.rutmiit.dto.ShowProjectInfoDto;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.Role;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.repositories.MasterRepository;
import ru.rutmiit.repositories.ProjectRepository;
import ru.rutmiit.repositories.UserRoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MasterServiceImpl implements MasterService{

    private final MasterRepository masterRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper mapper;
    private final AuthService authService;

    private final UserRoleRepository userRoleRepository;
    public MasterServiceImpl(MasterRepository masterRepository, ProjectRepository projectRepository, ModelMapper mapper, AuthService authService, UserRoleRepository userRoleRepository) {
        this.masterRepository = masterRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
        this.authService = authService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "projects", allEntries = true)
    public void addMaster(AddMasterDto masterDto) {
        log.info("Добавление нового мастера");
        UserRoles role = UserRoles.USER;
        if (masterDto.getIsBrigadier()){
            role = UserRoles.MODERATOR;
        }
        User createdUser = authService.registerMaster(
                masterDto.getEmail(),
                masterDto.getFio(),
                masterDto.getPassword(),
                role
        );
        Master master = mapper.map(masterDto, Master.class);
        master.setUser(createdUser);
        masterRepository.save(master);
        log.info("Мастер успешно добавлен");
    }

    @Override
    public Page<ShowMasterInfoDto> allMasterPaginated(Pageable pageable) {
        log.debug("Получение компаний с пагинацией: страница {}, размер {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return masterRepository.findAll(pageable)
                .map(master -> mapper.map(master, ShowMasterInfoDto.class));
    }
    @Override
    public List<Master> getAllMasters() {
        return masterRepository.findByIsBrigadier(false);
    }

    @Override
    public List<Master> getBrigadiers() {
        return masterRepository.findByIsBrigadier(true);
    }

    @Override
    public Optional<Master> getBrigadierById(String id) {
        return masterRepository.findById(id);
    }

    public String getProjectIds(String brigadierId) {
        return masterRepository.findById(brigadierId).stream()
                .map(master -> mapper.map(master, String.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    public long countAllMasters() {
        return masterRepository.count();
    }

    @Override
    public List<ShowMasterInfoDto> searchMaster(String searchTerm) {
        List<ShowMasterInfoDto> showProjectInfoDtos2 = masterRepository.searchByFioOrRoleOrCity(searchTerm).stream()
                .map(company -> mapper.map(company, ShowMasterInfoDto.class))
                .collect(Collectors.toList());
        return showProjectInfoDtos2;
    }

    @Override
    public Page<ShowMasterInfoDto> findAllFiltered(String search, boolean brigadiersOnly, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Master> masters;

        if (brigadiersOnly) {
            masters = masterRepository.findByIsBrigadierTrue(pageable);
        } else {
            masters = masterRepository.findAll(pageable);
        }

        return masters.map(m -> mapper.map(m, ShowMasterInfoDto.class));
    }


    @Override
    @Transactional
    public void setBrigadier(String email, boolean isBrigadier) {
        Master m = masterRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));
        m.setIsBrigadier(isBrigadier);
        User user = m.getUser();
        var masterRole = userRoleRepository
                .findRoleByName(isBrigadier ? UserRoles.MODERATOR : UserRoles.USER)
                .orElseThrow();

        user.setRoles(List.of(masterRole));
    }

    @Override
    public Page<ShowMasterInfoDto> findBrigadiersPaginated(Pageable pageable) {
        return masterRepository.findByIsBrigadierTrue(pageable)
                .map(m -> mapper.map(m, ShowMasterInfoDto.class));
    }


}

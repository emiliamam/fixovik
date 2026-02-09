package ru.rutmiit.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.rutmiit.dto.AddMasterDto;
import ru.rutmiit.dto.ShowMasterInfoDto;
import ru.rutmiit.dto.ShowProjectInfoDto;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;

import java.util.List;
import java.util.Optional;

public interface MasterService {
    void addMaster(AddMasterDto masterDto);

    Page<ShowMasterInfoDto> allMasterPaginated(Pageable pageable);

    public List<Master> getAllMasters();

    public List<Master> getBrigadiers();

    Optional<Master> getBrigadierById(String id);

    public String  getProjectIds(String brigadierId);

    public long countAllMasters();

    List<ShowMasterInfoDto> searchMaster(String searchTerm);

    Page<ShowMasterInfoDto> findAllFiltered(String search, boolean brigadiersOnly, int page, int size);

    void setBrigadier(String id, boolean isBrigadier);

    Page<ShowMasterInfoDto> findBrigadiersPaginated(Pageable pageable);


}

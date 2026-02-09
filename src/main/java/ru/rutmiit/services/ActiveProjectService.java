package ru.rutmiit.services;

import ru.rutmiit.dto.ShowActiveProjectDto;
import ru.rutmiit.dto.ShowProjectTask;

import java.util.List;

public interface ActiveProjectService {
    public List<ShowActiveProjectDto> allCompanies(String brigadierId);

    public List<ShowProjectTask> projectTask(String projectId);

    public List<ShowActiveProjectDto> allCompaniesForMaster(String masterId);
}

package ru.rutmiit.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findByName(String name);

    Optional<Project> findFirstByBrigadierIdAndStatus(String brigadierId, Status status);
    boolean existsByBrigadierIdAndStatus(String brigadierId, Status status);

    @Query("SELECT c FROM Project c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Project> searchByNameOrDescriptionOrAddress(@Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    void deleteByName(String name);

    boolean existsByName(String name);

    long countByStatus(Status status);

    Page<Project> findByStatus(Status status, Pageable pageable);

    Page<Project> findByStartDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<Project> findByStatusAndStartDateBetween(Status status, LocalDate from, LocalDate to, Pageable pageable);

    Page<Project> findByStartDateGreaterThanEqual(LocalDate from, Pageable pageable);

    Page<Project> findByStartDateLessThanEqual(LocalDate to, Pageable pageable);

    Page<Project> findByStatusAndStartDateGreaterThanEqual(Status status, LocalDate from, Pageable pageable);

    Page<Project> findByStatusAndStartDateLessThanEqual(Status status, LocalDate to, Pageable pageable);


}

package ru.rutmiit.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;
import ru.rutmiit.models.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasterRepository extends JpaRepository<Master, String> {
    List<Master> findByIsBrigadier(Boolean isBrigadier);

    Optional<Master> findByUser(User user);

    Optional<Master> findByUser_Username(String username);

    Optional<Master> findByUser_Email(String email);

    @Query("SELECT c FROM Master c WHERE " +
            "LOWER(c.fio) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Master> searchByFioOrRoleOrCity(@Param("searchTerm") String searchTerm);

    Page<Master> findByIsBrigadierTrue(Pageable pageable);


}

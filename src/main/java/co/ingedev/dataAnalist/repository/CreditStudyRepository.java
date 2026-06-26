package co.ingedev.dataAnalist.repository;

import co.ingedev.dataAnalist.entity.CreditStudy;
import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditStudyRepository extends JpaRepository<CreditStudy, UUID> {

    Page<CreditStudy> findByUser(User user, Pageable pageable);

    Page<CreditStudy> findByStatus(StudyStatus status, Pageable pageable);

    Page<CreditStudy> findByUserAndStatus(User user, StudyStatus status, Pageable pageable);

    Optional<CreditStudy> findByIdAndUser(UUID id, User user);

    boolean existsByIdAndUser(UUID id, User user);
}

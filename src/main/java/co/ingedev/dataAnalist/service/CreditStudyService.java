package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.request.CreditStudyRequest;
import co.ingedev.dataAnalist.dto.response.CreditStudyResponse;
import co.ingedev.dataAnalist.enums.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface CreditStudyService {
    Page<CreditStudyResponse> getAll(Authentication auth, StudyStatus status, Pageable pageable);
    CreditStudyResponse getById(UUID id, Authentication auth);
    CreditStudyResponse create(CreditStudyRequest request, Authentication auth);
    CreditStudyResponse update(UUID id, CreditStudyRequest request, Authentication auth);
    void delete(UUID id);
}

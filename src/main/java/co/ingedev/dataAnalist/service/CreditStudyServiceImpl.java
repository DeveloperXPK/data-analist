package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.request.CreditStudyRequest;
import co.ingedev.dataAnalist.dto.response.CreditStudyResponse;
import co.ingedev.dataAnalist.entity.CreditStudy;
import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.Role;
import co.ingedev.dataAnalist.enums.StudyStatus;
import co.ingedev.dataAnalist.exception.ResourceNotFoundException;
import co.ingedev.dataAnalist.exception.UnauthorizedException;
import co.ingedev.dataAnalist.repository.CreditStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditStudyServiceImpl implements CreditStudyService {

    private final CreditStudyRepository studyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CreditStudyResponse> getAll(Authentication auth, StudyStatus status, Pageable pageable) {
        User user = extractUser(auth);
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (status != null) {
            return isAdmin
                    ? studyRepository.findByStatus(status, pageable).map(CreditStudyResponse::from)
                    : studyRepository.findByUserAndStatus(user, status, pageable).map(CreditStudyResponse::from);
        }

        return isAdmin
                ? studyRepository.findAll(pageable).map(CreditStudyResponse::from)
                : studyRepository.findByUser(user, pageable).map(CreditStudyResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditStudyResponse getById(UUID id, Authentication auth) {
        User user = extractUser(auth);
        CreditStudy study = findStudyById(id);

        if (user.getRole() != Role.ADMIN && !study.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have access to this study");
        }

        return CreditStudyResponse.from(study);
    }

    @Override
    @Transactional
    public CreditStudyResponse create(CreditStudyRequest request, Authentication auth) {
        User user = extractUser(auth);

        CreditStudy study = CreditStudy.builder()
                .score(request.score())
                .institution(request.institution())
                .status(request.status())
                .requestedAmount(request.requestedAmount())
                .approvedAmount(request.approvedAmount())
                .user(user)
                .build();

        return CreditStudyResponse.from(studyRepository.save(study));
    }

    @Override
    @Transactional
    public CreditStudyResponse update(UUID id, CreditStudyRequest request, Authentication auth) {
        User user = extractUser(auth);
        CreditStudy study = findStudyById(id);

        if (user.getRole() != Role.ADMIN && !study.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have access to this study");
        }

        study.setScore(request.score());
        study.setInstitution(request.institution());
        study.setStatus(request.status());
        study.setRequestedAmount(request.requestedAmount());
        study.setApprovedAmount(request.approvedAmount());

        return CreditStudyResponse.from(studyRepository.save(study));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!studyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Credit study not found: " + id);
        }
        studyRepository.deleteById(id);
    }

    private User extractUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new UnauthorizedException("Authentication required");
        }
        return user;
    }

    private CreditStudy findStudyById(UUID id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit study not found: " + id));
    }
}

package co.ingedev.dataAnalist.dto.response;

import co.ingedev.dataAnalist.entity.CreditStudy;
import co.ingedev.dataAnalist.enums.StudyStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditStudyResponse(
        UUID id,
        Integer score,
        String institution,
        StudyStatus status,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        UUID userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CreditStudyResponse from(CreditStudy study) {
        return new CreditStudyResponse(
                study.getId(),
                study.getScore(),
                study.getInstitution(),
                study.getStatus(),
                study.getRequestedAmount(),
                study.getApprovedAmount(),
                study.getUser().getId(),
                study.getUser().getUsername(),
                study.getCreatedAt(),
                study.getUpdatedAt()
        );
    }
}

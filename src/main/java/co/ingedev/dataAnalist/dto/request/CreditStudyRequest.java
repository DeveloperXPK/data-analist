package co.ingedev.dataAnalist.dto.request;

import co.ingedev.dataAnalist.enums.StudyStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreditStudyRequest(
        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score must be >= 0")
        @Max(value = 1000, message = "Score must be <= 1000")
        Integer score,

        @NotBlank(message = "Institution is required")
        @Size(max = 100, message = "Institution name must not exceed 100 characters")
        String institution,

        @NotNull(message = "Status is required")
        StudyStatus status,

        @NotNull(message = "Requested amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Requested amount must be > 0")
        BigDecimal requestedAmount,

        @DecimalMin(value = "0.0", inclusive = false, message = "Approved amount must be > 0")
        BigDecimal approvedAmount
) {}

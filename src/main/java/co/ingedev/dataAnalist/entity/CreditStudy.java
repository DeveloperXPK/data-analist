package co.ingedev.dataAnalist.entity;

import co.ingedev.dataAnalist.enums.StudyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_studies")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(0)
    @Max(1000)
    @Column(nullable = false)
    private Integer score;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StudyStatus status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

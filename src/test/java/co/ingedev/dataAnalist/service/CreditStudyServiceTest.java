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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditStudyServiceTest {

    @Mock
    private CreditStudyRepository studyRepository;

    @InjectMocks
    private CreditStudyServiceImpl creditStudyService;

    private User adminUser;
    private User regularUser;
    private User otherUser;
    private CreditStudy study;

    @BeforeEach
    void setUp() {
        adminUser = User.builder().username("admin").email("admin@test.com")
                .password("pass").role(Role.ADMIN).build();

        regularUser = User.builder().username("user1").email("user1@test.com")
                .password("pass").role(Role.USER).build();

        otherUser = User.builder().username("user2").email("user2@test.com")
                .password("pass").role(Role.USER).build();

        study = CreditStudy.builder()
                .id(UUID.randomUUID())
                .score(750)
                .institution("Banco A")
                .status(StudyStatus.APROBADO)
                .requestedAmount(new BigDecimal("10000"))
                .approvedAmount(new BigDecimal("8000"))
                .user(regularUser)
                .build();
    }

    @Test
    void getAll_AsAdmin_ShouldCallFindAll() {
        Authentication auth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        when(studyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(study)));

        var result = creditStudyService.getAll(auth, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(studyRepository).findAll(any(Pageable.class));
        verify(studyRepository, never()).findByUser(any(), any());
    }

    @Test
    void getAll_AsUser_ShouldCallFindByUser() {
        Authentication auth = new UsernamePasswordAuthenticationToken(regularUser, null, regularUser.getAuthorities());
        when(studyRepository.findByUser(eq(regularUser), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(study)));

        var result = creditStudyService.getAll(auth, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        verify(studyRepository).findByUser(eq(regularUser), any(Pageable.class));
        verify(studyRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getById_AsUser_WithOwnStudy_ShouldReturnStudy() {
        Authentication auth = new UsernamePasswordAuthenticationToken(regularUser, null, regularUser.getAuthorities());
        when(studyRepository.findById(study.getId())).thenReturn(Optional.of(study));

        CreditStudyResponse response = creditStudyService.getById(study.getId(), auth);

        assertThat(response.id()).isEqualTo(study.getId());
    }

    @Test
    void getById_AsUser_WithOtherUsersStudy_ShouldThrowUnauthorized() {
        Authentication auth = new UsernamePasswordAuthenticationToken(otherUser, null, otherUser.getAuthorities());
        when(studyRepository.findById(study.getId())).thenReturn(Optional.of(study));

        assertThatThrownBy(() -> creditStudyService.getById(study.getId(), auth))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void getById_WithNonExistentId_ShouldThrowResourceNotFound() {
        UUID randomId = UUID.randomUUID();
        Authentication auth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        when(studyRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditStudyService.getById(randomId, auth))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_ShouldAssignAuthenticatedUser() {
        CreditStudyRequest request = new CreditStudyRequest(
                700, "Banco B", StudyStatus.PENDIENTE, new BigDecimal("5000"), null);
        Authentication auth = new UsernamePasswordAuthenticationToken(regularUser, null, regularUser.getAuthorities());
        when(studyRepository.save(any(CreditStudy.class))).thenAnswer(inv -> inv.getArgument(0));

        creditStudyService.create(request, auth);

        verify(studyRepository).save(argThat(s -> s.getUser().equals(regularUser)));
    }

    @Test
    void delete_WithNonExistentId_ShouldThrowResourceNotFound() {
        UUID randomId = UUID.randomUUID();
        when(studyRepository.existsById(randomId)).thenReturn(false);

        assertThatThrownBy(() -> creditStudyService.delete(randomId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studyRepository, never()).deleteById(any());
    }
}

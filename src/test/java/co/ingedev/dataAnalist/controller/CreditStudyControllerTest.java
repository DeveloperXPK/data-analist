package co.ingedev.dataAnalist.controller;

import co.ingedev.dataAnalist.config.SecurityConfig;
import co.ingedev.dataAnalist.dto.response.CreditStudyResponse;
import co.ingedev.dataAnalist.enums.StudyStatus;
import co.ingedev.dataAnalist.service.CreditStudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class CreditStudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditStudyService creditStudyService;

    private CreditStudyResponse sampleResponse() {
        return new CreditStudyResponse(
                UUID.randomUUID(), 750, "Banco A", StudyStatus.APROBADO,
                new BigDecimal("10000"), new BigDecimal("8000"),
                UUID.randomUUID(), "john",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getAll_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/studies"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_AsUser_ShouldReturn200() throws Exception {
        when(creditStudyService.getAll(any(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/v1/studies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_AsAdmin_ShouldReturn200() throws Exception {
        when(creditStudyService.getAll(any(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/v1/studies"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_AsUser_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/users/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_AsAdmin_ShouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/studies/" + id))
                .andExpect(status().isNoContent());

        verify(creditStudyService).delete(id);
    }
}

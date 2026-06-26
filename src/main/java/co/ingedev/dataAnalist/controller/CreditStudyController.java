package co.ingedev.dataAnalist.controller;

import co.ingedev.dataAnalist.dto.request.CreditStudyRequest;
import co.ingedev.dataAnalist.dto.response.CreditStudyResponse;
import co.ingedev.dataAnalist.enums.StudyStatus;
import co.ingedev.dataAnalist.service.CreditStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/studies")
@RequiredArgsConstructor
@Tag(name = "Credit Studies", description = "Credit study management")
@SecurityRequirement(name = "bearerAuth")
public class CreditStudyController {

    private final CreditStudyService creditStudyService;

    @GetMapping
    @Operation(summary = "List studies — ADMIN sees all, USER sees only their own")
    public ResponseEntity<Page<CreditStudyResponse>> getAll(
            @RequestParam(required = false) StudyStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            Authentication auth) {
        return ResponseEntity.ok(creditStudyService.getAll(auth, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a study by ID")
    public ResponseEntity<CreditStudyResponse> getById(@PathVariable UUID id, Authentication auth) {
        return ResponseEntity.ok(creditStudyService.getById(id, auth));
    }

    @PostMapping
    @Operation(summary = "Create a new credit study")
    public ResponseEntity<CreditStudyResponse> create(@Valid @RequestBody CreditStudyRequest request,
                                                       Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditStudyService.create(request, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a credit study")
    public ResponseEntity<CreditStudyResponse> update(@PathVariable UUID id,
                                                       @Valid @RequestBody CreditStudyRequest request,
                                                       Authentication auth) {
        return ResponseEntity.ok(creditStudyService.update(id, request, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a credit study (ADMIN only)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        creditStudyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

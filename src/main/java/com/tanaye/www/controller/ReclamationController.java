package com.tanaye.www.controller;

import com.tanaye.www.dto.ReclamationRequest;
import com.tanaye.www.entity.Reclamation;
import com.tanaye.www.service.ReclamationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reclamations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Réclamations", description = "Réclamations en cas de perte de colis, conformément aux CGU")
public class ReclamationController {

    private final ReclamationService reclamationService;

    @PostMapping
    @Operation(summary = "Créer une réclamation (confirmation des CGU requise)")
    public ResponseEntity<Reclamation> creer(@RequestBody @Valid ReclamationRequest req) {
        Reclamation r = reclamationService.creer(
                req.expediteurId(), req.colisId(), req.objet(), req.description(),
                Boolean.TRUE.equals(req.accepteCgu()));
        return ResponseEntity.ok(r);
    }

    @GetMapping
    @Operation(summary = "Lister les réclamations d'un expéditeur")
    public ResponseEntity<Page<Reclamation>> listerParExpediteur(@RequestParam Long expediteurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reclamationService.listerParExpediteur(expediteurId, pageable));
    }
}

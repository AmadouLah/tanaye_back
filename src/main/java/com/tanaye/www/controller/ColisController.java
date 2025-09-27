package com.tanaye.www.controller;

import com.tanaye.www.dto.ColisDTO;
import com.tanaye.www.dto.ColisRechercheDTO;
import com.tanaye.www.dto.ColisStatutDTO;
import com.tanaye.www.dto.ColisAffectationVoyageDTO;
import com.tanaye.www.entity.Colis;
import com.tanaye.www.service.ColisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Colis", description = "Gestion des colis et recherche")
public class ColisController {

    private final ColisService colisService;

    @PostMapping
    @Operation(summary = "Créer un colis")
    public ResponseEntity<Colis> creer(@RequestBody ColisDTO dto) {
        return ResponseEntity.ok(colisService.creer(dto.expediteurId(), dto.toEntity()));
    }

    @PostMapping("/recherche")
    @Operation(summary = "Recherche de colis selon critères")
    public ResponseEntity<Page<Colis>> rechercher(@RequestBody ColisRechercheDTO dto) {
        Pageable pageable = PageRequest.of(dto.page() != null ? dto.page() : 0, dto.size() != null ? dto.size() : 12);
        return ResponseEntity.ok(colisService.rechercher(dto.villeDepartId(), dto.villeArriveeId(), dto.expediteurId(),
                dto.destinataireId(), dto.voyageId(), dto.statut(), dto.type(), pageable));
    }

    @PutMapping("/{colisId}/statut")
    @Operation(summary = "Changer le statut d'un colis")
    public ResponseEntity<Colis> changerStatut(@PathVariable Long colisId, @RequestBody ColisStatutDTO dto) {
        return ResponseEntity.ok(colisService.changerStatut(colisId, dto.statut()));
    }

    @PutMapping("/{colisId}/affecter-voyage")
    @Operation(summary = "Affecter un colis à un voyage (vérif statut/capacité)")
    public ResponseEntity<Colis> affecterVoyage(@PathVariable Long colisId,
            @RequestBody ColisAffectationVoyageDTO dto) {
        return ResponseEntity.ok(colisService.affecterAuVoyage(colisId, dto.voyageId()));
    }

}

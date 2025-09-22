package com.tanaye.www.controller;

import com.tanaye.www.dto.LocalisationDTO;
import com.tanaye.www.dto.LocalisationRechercheDTO;
import com.tanaye.www.entity.Localisation;
import com.tanaye.www.service.LocalisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/localisations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Localisations", description = "Historique des positions des utilisateurs")
public class LocalisationController {

    private final LocalisationService localisationService;

    @PostMapping
    @Operation(summary = "Enregistrer une position utilisateur")
    public ResponseEntity<Localisation> enregistrer(@RequestBody LocalisationDTO dto) {
        return ResponseEntity.ok(localisationService.enregistrer(dto.utilisateurId(), dto.latitude(), dto.longitude()));
    }

    @GetMapping
    @Operation(summary = "Historique des positions par utilisateur")
    public ResponseEntity<Page<Localisation>> historique(@RequestParam Long utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(localisationService.historique(utilisateurId, pageable));
    }

    @PostMapping("/recherche")
    @Operation(summary = "Recherche avancée de localisations (user/date/bounding box)")
    public ResponseEntity<Page<Localisation>> recherche(@RequestBody LocalisationRechercheDTO dto) {
        Pageable pageable = PageRequest.of(dto.page() != null ? dto.page() : 0, dto.size() != null ? dto.size() : 12);
        if (dto.utilisateurId() != null && dto.debut() != null && dto.fin() != null) {
            return ResponseEntity.ok(localisationService.historiqueEntre(dto.utilisateurId(), dto.debut(), dto.fin(), pageable));
        }
        if (dto.debut() != null && dto.fin() != null && dto.minLat() != null && dto.maxLat() != null && dto.minLng() != null && dto.maxLng() != null) {
            return ResponseEntity.ok(localisationService.rechercheBoite(dto.debut(), dto.fin(), dto.minLat(), dto.maxLat(), dto.minLng(), dto.maxLng(), pageable));
        }
        return ResponseEntity.ok(Page.empty(pageable));
    }
}

package com.tanaye.www.controller;

import com.tanaye.www.dto.VoyageDTO;
import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.StatutVoyage;
import com.tanaye.www.service.VoyageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voyages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Voyages", description = "Gestion des voyages")
public class VoyageController {

    private final VoyageService voyageService;

    @PostMapping
    @Operation(summary = "Créer un voyage")
    public ResponseEntity<Voyage> creer(@RequestBody VoyageDTO dto) {
        return ResponseEntity.ok(voyageService.creer(dto.voyageurId(), dto.villeDepartId(), dto.villeArriveeId(), dto.toEntity()));
    }

    @GetMapping
    @Operation(summary = "Lister par trajet")
    public ResponseEntity<Page<Voyage>> listerParTrajet(@RequestParam Long villeDepartId,
            @RequestParam Long villeArriveeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(voyageService.listerParTrajet(villeDepartId, villeArriveeId, page, size));
    }

    @GetMapping("/statut")
    @Operation(summary = "Lister par statut")
    public ResponseEntity<Page<Voyage>> listerParStatut(@RequestParam StatutVoyage statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(voyageService.listerParStatut(statut, page, size));
    }

    @GetMapping("/voyageur/{voyageurId}")
    @Operation(summary = "Lister par voyageur")
    public ResponseEntity<Page<Voyage>> listerParVoyageur(@PathVariable Long voyageurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(voyageService.listerParVoyageur(voyageurId, page, size));
    }
}

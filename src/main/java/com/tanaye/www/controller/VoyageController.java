package com.tanaye.www.controller;

import com.tanaye.www.dto.VoyageDTO;
import com.tanaye.www.dto.VoyageRechercheDTO;
import com.tanaye.www.dto.VoyageStatistiquesDTO;
import com.tanaye.www.entity.Voyage;
import com.tanaye.www.enums.ModeTransport;
import com.tanaye.www.enums.StatutVoyage;
import com.tanaye.www.enums.TypeVoyage;
import com.tanaye.www.service.VoyageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity
                .ok(voyageService.creer(dto.voyageurId(), dto.villeDepartId(), dto.villeArriveeId(), dto.toEntity()));
    }

    @PostMapping("/avec-details")
    @Operation(summary = "Créer un voyage avec type et mode de transport")
    public ResponseEntity<Voyage> creerAvecDetails(@RequestParam Long voyageurId, @RequestParam Long villeDepartId,
            @RequestParam Long villeArriveeId, @RequestBody VoyageDTO dto, @RequestParam TypeVoyage type,
            @RequestParam ModeTransport modeTransport) {
        return ResponseEntity.ok(
                voyageService.creer(voyageurId, villeDepartId, villeArriveeId, dto.toEntity(), type, modeTransport));
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un voyage")
    public ResponseEntity<Voyage> changerStatut(@PathVariable Long id, @RequestParam StatutVoyage statut) {
        return ResponseEntity.ok(voyageService.changerStatut(id, statut));
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

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de voyages")
    public ResponseEntity<Page<Voyage>> rechercher(@RequestBody VoyageRechercheDTO criteres, Pageable pageable) {
        return ResponseEntity.ok(voyageService.rechercher(criteres, pageable));
    }

    @GetMapping("/voyageur/{voyageurId}/futurs")
    @Operation(summary = "Voyages futurs d'un voyageur")
    public ResponseEntity<List<Voyage>> futursParVoyageur(@PathVariable Long voyageurId) {
        return ResponseEntity.ok(voyageService.futursParVoyageur(voyageurId));
    }

    @GetMapping("/voyageur/{voyageurId}/passes")
    @Operation(summary = "Voyages passés d'un voyageur")
    public ResponseEntity<List<Voyage>> passesParVoyageur(@PathVariable Long voyageurId) {
        return ResponseEntity.ok(voyageService.passesParVoyageur(voyageurId));
    }

    @GetMapping("/voyageur/{voyageurId}/count/statut/{statut}")
    @Operation(summary = "Compter les voyages par statut pour un voyageur")
    public ResponseEntity<Long> countParStatut(@PathVariable Long voyageurId, @PathVariable StatutVoyage statut) {
        return ResponseEntity.ok(voyageService.countParVoyageurEtStatut(voyageurId, statut));
    }

    @GetMapping("/voyageur/{voyageurId}/statistiques")
    @Operation(summary = "Statistiques des voyages par statut")
    public ResponseEntity<List<VoyageStatistiquesDTO>> statistiquesParStatut(@PathVariable Long voyageurId) {
        return ResponseEntity.ok(voyageService.statistiquesParStatut(voyageurId));
    }

    @DeleteMapping("/nettoyer-planifies-depasses")
    @Operation(summary = "Nettoyer les voyages planifiés dépassés")
    public ResponseEntity<Integer> nettoyerPlanifiesDepasses() {
        int count = voyageService.nettoyerPlanifiesDepasses();
        return ResponseEntity.ok(count);
    }

    @PutMapping("/marquer-en-retard")
    @Operation(summary = "Marquer les voyages en retard")
    public ResponseEntity<Integer> marquerEnRetard() {
        int count = voyageService.marquerEnRetard();
        return ResponseEntity.ok(count);
    }
}

package com.tanaye.www.controller;

import com.tanaye.www.dto.ReclamationRechercheDTO;
import com.tanaye.www.dto.ReclamationRequest;
import com.tanaye.www.dto.ReclamationStatistiquesDTO;
import com.tanaye.www.entity.Reclamation;
import com.tanaye.www.enums.StatutReclamation;
import com.tanaye.www.enums.TypeReclamation;
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

import java.util.List;

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

    @PostMapping("/avec-type")
    @Operation(summary = "Créer une réclamation avec type spécifique")
    public ResponseEntity<Reclamation> creerAvecType(@RequestParam Long expediteurId, @RequestParam Long colisId,
            @RequestParam String objet, @RequestParam String description, @RequestParam boolean accepteCgu,
            @RequestParam TypeReclamation type) {
        Reclamation r = reclamationService.creer(expediteurId, colisId, objet, description, accepteCgu, type);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'une réclamation")
    public ResponseEntity<Reclamation> changerStatut(@PathVariable Long id, @RequestParam StatutReclamation statut,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(reclamationService.changerStatut(id, statut, commentaire));
    }

    @GetMapping
    @Operation(summary = "Lister les réclamations d'un expéditeur")
    public ResponseEntity<Page<Reclamation>> listerParExpediteur(@RequestParam Long expediteurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reclamationService.listerParExpediteur(expediteurId, pageable));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de réclamations")
    public ResponseEntity<Page<Reclamation>> rechercher(@RequestBody ReclamationRechercheDTO criteres,
            Pageable pageable) {
        return ResponseEntity.ok(reclamationService.rechercher(criteres, pageable));
    }

    @GetMapping("/expediteur/{expediteurId}/count/statut/{statut}")
    @Operation(summary = "Compter les réclamations par statut pour un expéditeur")
    public ResponseEntity<Long> countParStatut(@PathVariable Long expediteurId,
            @PathVariable StatutReclamation statut) {
        return ResponseEntity.ok(reclamationService.countParExpediteurEtStatut(expediteurId, statut));
    }

    @GetMapping("/expediteur/{expediteurId}/count/type/{type}")
    @Operation(summary = "Compter les réclamations par type pour un expéditeur")
    public ResponseEntity<Long> countParType(@PathVariable Long expediteurId, @PathVariable TypeReclamation type) {
        return ResponseEntity.ok(reclamationService.countParExpediteurEtType(expediteurId, type));
    }

    @GetMapping("/expediteur/{expediteurId}/statistiques/statut")
    @Operation(summary = "Statistiques des réclamations par statut")
    public ResponseEntity<List<ReclamationStatistiquesDTO>> statistiquesParStatut(@PathVariable Long expediteurId) {
        return ResponseEntity.ok(reclamationService.statistiquesParStatut(expediteurId));
    }

    @GetMapping("/expediteur/{expediteurId}/statistiques/type")
    @Operation(summary = "Statistiques des réclamations par type")
    public ResponseEntity<List<ReclamationStatistiquesDTO>> statistiquesParType(@PathVariable Long expediteurId) {
        return ResponseEntity.ok(reclamationService.statistiquesParType(expediteurId));
    }

    @GetMapping("/colis/{colisId}/actives")
    @Operation(summary = "Réclamations actives pour un colis")
    public ResponseEntity<List<Reclamation>> reclamationsActivesParColis(@PathVariable Long colisId) {
        return ResponseEntity.ok(reclamationService.reclamationsActivesParColis(colisId));
    }

    @DeleteMapping("/nettoyer-en-attente")
    @Operation(summary = "Nettoyer les réclamations en attente anciennes")
    public ResponseEntity<Integer> nettoyerEnAttenteAnciennes(@RequestParam(defaultValue = "30") int joursRetention) {
        int count = reclamationService.nettoyerEnAttenteAnciennes(joursRetention);
        return ResponseEntity.ok(count);
    }
}

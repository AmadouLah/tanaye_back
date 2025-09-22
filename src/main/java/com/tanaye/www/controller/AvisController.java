package com.tanaye.www.controller;

import com.tanaye.www.dto.AvisDTO;
import com.tanaye.www.dto.AvisStatsDTO;
import com.tanaye.www.repository.AvisRepository.TopVoyageurProjection;
import com.tanaye.www.entity.Avis;
import com.tanaye.www.service.AvisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Avis", description = "Gestion des avis entre utilisateurs")
public class AvisController {

    private final AvisService avisService;

    @PostMapping
    @Operation(summary = "Créer un avis")
    public ResponseEntity<Avis> creer(@RequestBody AvisDTO dto) {
        return ResponseEntity
                .ok(avisService.creer(dto.auteurId(), dto.destinataireId(), dto.note(), dto.commentaire()));
    }

    @GetMapping("/destinataire/{id}")
    @Operation(summary = "Lister avis reçus par utilisateur")
    public ResponseEntity<Page<Avis>> recu(@PathVariable("id") Long destinataireId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(avisService.listerParDestinataire(destinataireId, pageable));
    }

    @GetMapping("/auteur/{id}")
    @Operation(summary = "Lister avis donnés par utilisateur")
    public ResponseEntity<Page<Avis>> donne(@PathVariable("id") Long auteurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(avisService.listerParAuteur(auteurId, pageable));
    }

    @GetMapping("/stats/{utilisateurId}")
    @Operation(summary = "Statistiques d'avis d'un utilisateur (moyenne, total, distribution)")
    public ResponseEntity<AvisStatsDTO> stats(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(avisService.statsPourUtilisateur(utilisateurId));
    }

    @GetMapping("/top-voyageurs")
    @Operation(summary = "Top voyageurs fiables (moyenne et volume d'avis)")
    public ResponseEntity<Page<TopVoyageurProjection>> topVoyageurs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "5") long minAvis) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(avisService.topVoyageursFiables(minAvis, pageable));
    }

    @PutMapping("/{avisId}")
    @Operation(summary = "Mettre à jour son avis")
    public ResponseEntity<Avis> maj(@PathVariable Long avisId,
            @RequestParam Long auteurId,
            @RequestParam(required = false) Integer note,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(avisService.mettreAJour(avisId, auteurId, note, commentaire));
    }

    @DeleteMapping("/{avisId}")
    @Operation(summary = "Supprimer son avis")
    public ResponseEntity<Void> supprimer(@PathVariable Long avisId, @RequestParam Long auteurId) {
        avisService.supprimer(avisId, auteurId);
        return ResponseEntity.noContent().build();
    }
}

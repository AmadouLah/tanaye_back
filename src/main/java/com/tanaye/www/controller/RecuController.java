package com.tanaye.www.controller;

import com.tanaye.www.dto.RecuDTO;
import com.tanaye.www.dto.RecuRechercheDTO;
import com.tanaye.www.dto.RecuStatistiquesDTO;
import com.tanaye.www.entity.Recu;
import com.tanaye.www.enums.StatutRecu;
import com.tanaye.www.enums.TypeRecu;
import com.tanaye.www.service.RecuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recus")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Reçus", description = "Reçus électroniques de paiement")
public class RecuController {

    private final RecuService recuService;

    @PostMapping("/generer/{paiementId}")
    @Operation(summary = "Générer un reçu après confirmation du paiement")
    public ResponseEntity<Recu> generer(@PathVariable Long paiementId) {
        return ResponseEntity.ok(recuService.genererApresConfirmation(paiementId));
    }

    @PostMapping("/generer/{paiementId}/avec-type")
    @Operation(summary = "Générer un reçu avec type spécifique")
    public ResponseEntity<Recu> genererAvecType(@PathVariable Long paiementId, @RequestParam TypeRecu type) {
        return ResponseEntity.ok(recuService.genererApresConfirmation(paiementId, type));
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un reçu")
    public ResponseEntity<Recu> changerStatut(@PathVariable Long id, @RequestParam StatutRecu statut) {
        return ResponseEntity.ok(recuService.changerStatut(id, statut));
    }

    @GetMapping
    @Operation(summary = "Lister les reçus d'un payeur")
    public ResponseEntity<Page<RecuDTO>> lister(@RequestParam Long payeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recuService.listerParPayeur(payeurId, pageable));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de reçus")
    public ResponseEntity<Page<Recu>> rechercher(@RequestBody RecuRechercheDTO criteres, Pageable pageable) {
        return ResponseEntity.ok(recuService.rechercher(criteres, pageable));
    }

    @GetMapping("/payeur/{payeurId}/count")
    @Operation(summary = "Compter les reçus d'un payeur")
    public ResponseEntity<Long> countParPayeur(@PathVariable Long payeurId) {
        return ResponseEntity.ok(recuService.countParPayeur(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/total-montant")
    @Operation(summary = "Total des montants des reçus d'un payeur")
    public ResponseEntity<BigDecimal> totalMontantParPayeur(@PathVariable Long payeurId) {
        return ResponseEntity.ok(recuService.totalMontantParPayeur(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/montant-moyen")
    @Operation(summary = "Montant moyen des reçus d'un payeur")
    public ResponseEntity<BigDecimal> montantMoyenParPayeur(@PathVariable Long payeurId) {
        return ResponseEntity.ok(recuService.montantMoyenParPayeur(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/recents")
    @Operation(summary = "Reçus récents d'un payeur")
    public ResponseEntity<List<Recu>> recentsParPayeur(@PathVariable Long payeurId,
            @RequestParam(defaultValue = "30") int joursRecents) {
        return ResponseEntity.ok(recuService.recentsParPayeur(payeurId, joursRecents));
    }

    @GetMapping("/payeur/{payeurId}/annee/{annee}")
    @Operation(summary = "Reçus d'un payeur pour une année")
    public ResponseEntity<List<Recu>> parPayeurEtAnnee(@PathVariable Long payeurId, @PathVariable int annee) {
        return ResponseEntity.ok(recuService.parPayeurEtAnnee(payeurId, annee));
    }

    @GetMapping("/payeur/{payeurId}/statistiques")
    @Operation(summary = "Statistiques des reçus par année")
    public ResponseEntity<List<RecuStatistiquesDTO>> statistiquesParAnnee(@PathVariable Long payeurId) {
        return ResponseEntity.ok(recuService.statistiquesParAnnee(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/paiement/{paiementId}")
    @Operation(summary = "Reçu pour un paiement spécifique")
    public ResponseEntity<Optional<Recu>> parPayeurEtPaiement(@PathVariable Long payeurId,
            @PathVariable Long paiementId) {
        return ResponseEntity.ok(recuService.parPayeurEtPaiement(payeurId, paiementId));
    }

    @DeleteMapping("/nettoyer-anciens")
    @Operation(summary = "Nettoyer les reçus anciens")
    public ResponseEntity<Integer> nettoyerAnciens(@RequestParam(defaultValue = "1825") int joursRetention) {
        int count = recuService.nettoyerAnciens(joursRetention);
        return ResponseEntity.ok(count);
    }
}

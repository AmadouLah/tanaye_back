package com.tanaye.www.controller;

import com.tanaye.www.dto.PaiementRechercheDTO;
import com.tanaye.www.dto.PaiementRequest;
import com.tanaye.www.dto.PaiementStatistiquesDTO;
import com.tanaye.www.entity.Paiement;
import com.tanaye.www.enums.ModePaiement;
import com.tanaye.www.service.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Paiements", description = "Gestion des paiements (CB, OM, SAMA)")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un paiement")
    public ResponseEntity<Paiement> creer(@RequestBody @Valid PaiementRequest req) {
        Paiement p = paiementService.creer(req.payeurId(), req.colisId(), req.mode(), req.montant());
        return ResponseEntity.ok(p);
    }

    @GetMapping
    @Operation(summary = "Lister les paiements par payeur")
    public ResponseEntity<Page<Paiement>> listerParPayeur(@RequestParam Long payeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paiementService.listerParPayeur(payeurId, pageable));
    }

    @GetMapping("/mode")
    @Operation(summary = "Lister par mode de paiement")
    public ResponseEntity<Page<Paiement>> listerParMode(@RequestParam ModePaiement mode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paiementService.listerParMode(mode, pageable));
    }

    @PostMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer un paiement (déclenche la génération du reçu)")
    public ResponseEntity<Paiement> confirmer(@PathVariable("id") Long paiementId,
            @RequestParam(required = false) String referenceExterne) {
        return ResponseEntity.ok(paiementService.confirmerPaiement(paiementId, referenceExterne));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de paiements")
    public ResponseEntity<Page<Paiement>> rechercher(@RequestBody PaiementRechercheDTO criteres, Pageable pageable) {
        return ResponseEntity.ok(paiementService.rechercher(criteres, pageable));
    }

    @GetMapping("/payeur/{payeurId}/statistiques")
    @Operation(summary = "Statistiques des paiements d'un payeur")
    public ResponseEntity<List<PaiementStatistiquesDTO>> statistiquesParMode(@PathVariable Long payeurId) {
        return ResponseEntity.ok(paiementService.statistiquesParMode(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/count/reussis")
    @Operation(summary = "Compter les paiements réussis d'un payeur")
    public ResponseEntity<Long> countReussis(@PathVariable Long payeurId) {
        return ResponseEntity.ok(paiementService.countReussis(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/count/echoues")
    @Operation(summary = "Compter les paiements échoués d'un payeur")
    public ResponseEntity<Long> countEchoues(@PathVariable Long payeurId) {
        return ResponseEntity.ok(paiementService.countEchoues(payeurId));
    }

    @GetMapping("/payeur/{payeurId}/total-montant")
    @Operation(summary = "Total des montants réussis d'un payeur")
    public ResponseEntity<BigDecimal> totalMontantReussi(@PathVariable Long payeurId) {
        return ResponseEntity.ok(paiementService.totalMontantReussi(payeurId));
    }

    @GetMapping("/colis/{colisId}/reussi")
    @Operation(summary = "Paiement réussi pour un colis")
    public ResponseEntity<Optional<Paiement>> paiementReussiParColis(@PathVariable Long colisId) {
        return ResponseEntity.ok(paiementService.paiementReussiParColis(colisId));
    }

    @DeleteMapping("/nettoyer-echoues")
    @Operation(summary = "Nettoyer les paiements échoués anciens")
    public ResponseEntity<Integer> nettoyerEchouesAnciens(@RequestParam(defaultValue = "30") int joursRetention) {
        int count = paiementService.nettoyerEchouesAnciens(joursRetention);
        return ResponseEntity.ok(count);
    }
}

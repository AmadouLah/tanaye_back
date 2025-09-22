package com.tanaye.www.controller;

import com.tanaye.www.dto.PaiementRequest;
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
}

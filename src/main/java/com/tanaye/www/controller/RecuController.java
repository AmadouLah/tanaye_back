package com.tanaye.www.controller;

import com.tanaye.www.dto.RecuDTO;
import com.tanaye.www.entity.Recu;
import com.tanaye.www.service.RecuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @Operation(summary = "Lister les reçus d'un payeur")
    public ResponseEntity<Page<RecuDTO>> lister(@RequestParam Long payeurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recuService.listerParPayeur(payeurId, pageable));
    }
}

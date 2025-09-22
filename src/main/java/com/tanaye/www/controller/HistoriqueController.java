package com.tanaye.www.controller;

import com.tanaye.www.dto.HistoriqueRechercheDTO;
import com.tanaye.www.entity.Historique;
import com.tanaye.www.service.HistoriqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historiques")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Historiques", description = "Consultation d'actions critiques")
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    @GetMapping
    @Operation(summary = "Lister par utilisateur")
    public ResponseEntity<Page<Historique>> listerParUtilisateur(@RequestParam Long utilisateurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(historiqueService.listerParUtilisateur(utilisateurId, pageable));
    }

    @GetMapping("/recherche")
    @Operation(summary = "Recherche par libellé d'action")
    public ResponseEntity<Page<Historique>> rechercher(@RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(historiqueService.rechercher(q, pageable));
    }

    @PostMapping("/recherche-avancee")
    @Operation(summary = "Recherche avancée (utilisateur, action, plage de dates)")
    public ResponseEntity<Page<Historique>> rechercheAvancee(@RequestBody HistoriqueRechercheDTO dto) {
        Pageable pageable = PageRequest.of(dto.page() != null ? dto.page() : 0, dto.size() != null ? dto.size() : 12);
        return ResponseEntity.ok(historiqueService.rechercherAvancee(dto.utilisateurId(), dto.action(), dto.debut(),
                dto.fin(), pageable));
    }

    @PostMapping(value = "/export/csv", produces = "text/csv;charset=UTF-8")
    @Operation(summary = "Exporter les historiques en CSV selon filtres")
    public ResponseEntity<String> exporterCsv(@RequestBody HistoriqueRechercheDTO dto) {
        String csv = historiqueService.exporterCsv(dto.utilisateurId(), dto.action(), dto.debut(), dto.fin());
        String bomCsv = "\uFEFF" + csv; // BOM UTF-8 pour Excel
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=historiques.csv")
                .body(bomCsv);
    }
}

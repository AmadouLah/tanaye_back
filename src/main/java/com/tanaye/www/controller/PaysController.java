package com.tanaye.www.controller;

import com.tanaye.www.dto.PaysDTO;
import com.tanaye.www.dto.PaysRechercheDTO;
import com.tanaye.www.entity.Pays;
import com.tanaye.www.enums.Continent;
import com.tanaye.www.service.PaysService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pays")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Pays", description = "Gestion des pays")
public class PaysController {

    private final PaysService paysService;

    @PostMapping
    @Operation(summary = "Créer un pays")
    public ResponseEntity<Pays> creer(@RequestBody PaysDTO dto) {
        return ResponseEntity.ok(paysService.creer(dto.toEntity()));
    }

    @PostMapping("/avec-continent")
    @Operation(summary = "Créer un pays avec continent")
    public ResponseEntity<Pays> creerAvecContinent(@RequestParam String nom, @RequestParam String codeIso,
            @RequestParam(required = false) Continent continent) {
        return ResponseEntity.ok(paysService.creer(nom, codeIso, continent));
    }

    @GetMapping
    @Operation(summary = "Rechercher des pays par nom")
    public ResponseEntity<Page<Pays>> rechercher(@RequestParam(required = false) String nom,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(paysService.rechercher(nom, page, size));
    }

    @GetMapping("/actifs")
    @Operation(summary = "Liste des pays actifs")
    public ResponseEntity<List<Pays>> listerActifs() {
        return ResponseEntity.ok(paysService.listerActifs());
    }

    @GetMapping("/code-iso/{codeIso}")
    @Operation(summary = "Pays par code ISO")
    public ResponseEntity<Optional<Pays>> parCodeIso(@PathVariable String codeIso) {
        return ResponseEntity.ok(paysService.parCodeIso(codeIso));
    }

    @GetMapping("/code-iso/{codeIso}/actif")
    @Operation(summary = "Pays actif par code ISO")
    public ResponseEntity<Optional<Pays>> parCodeIsoActif(@PathVariable String codeIso) {
        return ResponseEntity.ok(paysService.parCodeIsoActif(codeIso));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de pays")
    public ResponseEntity<Page<Pays>> rechercherAvancee(@RequestBody PaysRechercheDTO criteres, Pageable pageable) {
        return ResponseEntity.ok(paysService.rechercher(criteres, pageable));
    }

    @GetMapping("/rechercher-actifs")
    @Operation(summary = "Rechercher des pays actifs")
    public ResponseEntity<List<Pays>> rechercherActifs(@RequestParam String query) {
        return ResponseEntity.ok(paysService.rechercherActifs(query));
    }

    @PostMapping("/par-codes-iso")
    @Operation(summary = "Pays par codes ISO")
    public ResponseEntity<List<Pays>> parCodesIso(@RequestBody List<String> codes) {
        return ResponseEntity.ok(paysService.parCodesIso(codes));
    }

    @GetMapping("/continent/{continent}")
    @Operation(summary = "Pays par continent")
    public ResponseEntity<List<Pays>> parContinent(@PathVariable Continent continent) {
        return ResponseEntity.ok(paysService.parContinent(continent));
    }

    @GetMapping("/continents")
    @Operation(summary = "Liste des continents")
    public ResponseEntity<List<String>> tousContinents() {
        return ResponseEntity.ok(paysService.tousContinents());
    }

    @GetMapping("/count/actifs")
    @Operation(summary = "Compter les pays actifs")
    public ResponseEntity<Long> countActifs() {
        return ResponseEntity.ok(paysService.countActifs());
    }

    @PutMapping("/{paysId}/activer")
    @Operation(summary = "Activer un pays")
    public ResponseEntity<Pays> activer(@PathVariable Long paysId) {
        return ResponseEntity.ok(paysService.activer(paysId));
    }

    @PutMapping("/{paysId}/desactiver")
    @Operation(summary = "Désactiver un pays")
    public ResponseEntity<Pays> desactiver(@PathVariable Long paysId) {
        return ResponseEntity.ok(paysService.desactiver(paysId));
    }
}

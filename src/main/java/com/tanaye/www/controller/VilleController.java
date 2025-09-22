package com.tanaye.www.controller;

import com.tanaye.www.dto.VilleDTO;
import com.tanaye.www.dto.VilleRechercheDTO;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.enums.TypeVille;
import com.tanaye.www.service.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/villes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Villes", description = "Gestion des villes d'une région")
public class VilleController {

    private final VilleService villeService;

    @PostMapping
    @Operation(summary = "Créer une ville dans une région")
    public ResponseEntity<Ville> creer(@RequestParam Long regionId, @RequestBody VilleDTO dto) {
        return ResponseEntity.ok(villeService.creer(regionId, dto.toEntity()));
    }

    @PostMapping("/avec-type")
    @Operation(summary = "Créer une ville avec type spécifique")
    public ResponseEntity<Ville> creerAvecType(@RequestParam Long regionId, @RequestBody VilleDTO dto,
            @RequestParam TypeVille type) {
        return ResponseEntity.ok(villeService.creer(regionId, dto.toEntity(), type));
    }

    @GetMapping
    @Operation(summary = "Lister les villes d'une région")
    public ResponseEntity<Page<Ville>> lister(@RequestParam Long regionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(villeService.listerParRegion(regionId, page, size));
    }

    @GetMapping("/actifs")
    @Operation(summary = "Liste des villes actives")
    public ResponseEntity<List<Ville>> listerActifs() {
        return ResponseEntity.ok(villeService.listerActifs());
    }

    @GetMapping("/actifs/region/{regionId}")
    @Operation(summary = "Villes actives d'une région")
    public ResponseEntity<List<Ville>> listerActifsParRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(villeService.listerActifsParRegion(regionId));
    }

    @GetMapping("/rechercher")
    @Operation(summary = "Rechercher des villes par nom")
    public ResponseEntity<Page<Ville>> rechercher(@RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(villeService.rechercher(query, pageable));
    }

    @GetMapping("/rechercher/region/{regionId}")
    @Operation(summary = "Rechercher des villes dans une région")
    public ResponseEntity<Page<Ville>> rechercherParRegion(@PathVariable Long regionId, @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(villeService.rechercherParRegion(regionId, query, pageable));
    }

    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée de villes")
    public ResponseEntity<Page<Ville>> rechercherAvancee(@RequestBody VilleRechercheDTO criteres, Pageable pageable) {
        return ResponseEntity.ok(villeService.rechercher(criteres, pageable));
    }

    @GetMapping("/rechercher-actifs")
    @Operation(summary = "Rechercher des villes actives")
    public ResponseEntity<List<Ville>> rechercherActifs(@RequestParam String query) {
        return ResponseEntity.ok(villeService.rechercherActifs(query));
    }

    @GetMapping("/actifs/pays/{paysId}")
    @Operation(summary = "Villes actives d'un pays")
    public ResponseEntity<List<Ville>> listerActifsParPays(@PathVariable Long paysId) {
        return ResponseEntity.ok(villeService.listerActifsParPays(paysId));
    }

    @GetMapping("/actifs/pays/{paysId}/rechercher")
    @Operation(summary = "Rechercher des villes actives dans un pays")
    public ResponseEntity<List<Ville>> rechercherActifsParPays(@PathVariable Long paysId, @RequestParam String query) {
        return ResponseEntity.ok(villeService.rechercherActifsParPays(paysId, query));
    }

    @GetMapping("/actifs/code-pays/{codePays}")
    @Operation(summary = "Villes actives par code pays")
    public ResponseEntity<List<Ville>> listerActifsParCodePays(@PathVariable String codePays) {
        return ResponseEntity.ok(villeService.listerActifsParCodePays(codePays));
    }

    @GetMapping("/actifs/continent/{continent}")
    @Operation(summary = "Villes actives par continent")
    public ResponseEntity<List<Ville>> listerActifsParContinent(@PathVariable String continent) {
        return ResponseEntity.ok(villeService.listerActifsParContinent(continent));
    }

    @GetMapping("/count/actifs")
    @Operation(summary = "Compter les villes actives")
    public ResponseEntity<Long> countActifs() {
        return ResponseEntity.ok(villeService.countActifs());
    }

    @GetMapping("/count/actifs/region/{regionId}")
    @Operation(summary = "Compter les villes actives d'une région")
    public ResponseEntity<Long> countActifsParRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(villeService.countActifsParRegion(regionId));
    }

    @GetMapping("/nom/{nom}/region/{regionId}")
    @Operation(summary = "Ville par nom et région")
    public ResponseEntity<Optional<Ville>> parNomEtRegion(@PathVariable String nom, @PathVariable Long regionId) {
        return ResponseEntity.ok(villeService.parNomEtRegion(nom, regionId));
    }

    @GetMapping("/nom/{nom}/region/{regionId}/actif")
    @Operation(summary = "Ville active par nom et région")
    public ResponseEntity<Optional<Ville>> parNomEtRegionActif(@PathVariable String nom, @PathVariable Long regionId) {
        return ResponseEntity.ok(villeService.parNomEtRegionActif(nom, regionId));
    }

    @PutMapping("/{villeId}/activer")
    @Operation(summary = "Activer une ville")
    public ResponseEntity<Ville> activer(@PathVariable Long villeId) {
        return ResponseEntity.ok(villeService.activer(villeId));
    }

    @PutMapping("/{villeId}/desactiver")
    @Operation(summary = "Désactiver une ville")
    public ResponseEntity<Ville> desactiver(@PathVariable Long villeId) {
        return ResponseEntity.ok(villeService.desactiver(villeId));
    }
}

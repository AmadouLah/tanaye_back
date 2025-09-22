package com.tanaye.www.controller;

import com.tanaye.www.dto.RegionDTO;
import com.tanaye.www.service.RegionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.tanaye.www.entity.Region;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Régions", description = "Gestion des régions d'un pays")
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    @Operation(summary = "Créer une région dans un pays")
    public ResponseEntity<Region> creer(@RequestParam Long paysId, @RequestBody RegionDTO dto) {
        return ResponseEntity.ok(regionService.creer(paysId, dto.toEntity()));
    }

    @GetMapping
    @Operation(summary = "Lister les régions d'un pays")
    public ResponseEntity<Page<Region>> lister(@RequestParam Long paysId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(regionService.listerParPays(paysId, page, size));
    }

    @GetMapping("/rechercher")
    @Operation(summary = "Rechercher des régions")
    public ResponseEntity<Page<Region>> rechercher(@RequestParam(required = false) Long paysId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(regionService.rechercher(paysId, q, pageable));
    }

    @GetMapping("/actifs")
    @Operation(summary = "Lister les régions actives")
    public ResponseEntity<List<Region>> actifs() {
        return ResponseEntity.ok(regionService.listerActifs());
    }

    @GetMapping("/actifs/pays/{paysId}")
    @Operation(summary = "Lister les régions actives d'un pays")
    public ResponseEntity<List<Region>> actifsParPays(@PathVariable Long paysId) {
        return ResponseEntity.ok(regionService.listerActifsParPays(paysId));
    }

    @GetMapping("/count/actifs")
    @Operation(summary = "Compter les régions actives")
    public ResponseEntity<Long> countActifs() {
        return ResponseEntity.ok(regionService.countActifs());
    }

    @GetMapping("/count/actifs/pays/{paysId}")
    @Operation(summary = "Compter les régions actives d'un pays")
    public ResponseEntity<Long> countActifsParPays(@PathVariable Long paysId) {
        return ResponseEntity.ok(regionService.countActifsParPays(paysId));
    }

    @PutMapping("/{regionId}/activer")
    @Operation(summary = "Activer une région")
    public ResponseEntity<Region> activer(@PathVariable Long regionId) {
        return ResponseEntity.ok(regionService.activer(regionId));
    }

    @PutMapping("/{regionId}/desactiver")
    @Operation(summary = "Désactiver une région")
    public ResponseEntity<Region> desactiver(@PathVariable Long regionId) {
        return ResponseEntity.ok(regionService.desactiver(regionId));
    }
}

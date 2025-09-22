package com.tanaye.www.controller;

import com.tanaye.www.dto.RegionDTO;
import com.tanaye.www.entity.Region;
import com.tanaye.www.service.RegionService;
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
}

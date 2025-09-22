package com.tanaye.www.controller;

import com.tanaye.www.dto.VilleDTO;
import com.tanaye.www.entity.Ville;
import com.tanaye.www.service.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @Operation(summary = "Lister les villes d'une région")
    public ResponseEntity<Page<Ville>> lister(@RequestParam Long regionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(villeService.listerParRegion(regionId, page, size));
    }
}

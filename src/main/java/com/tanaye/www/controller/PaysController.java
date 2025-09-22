package com.tanaye.www.controller;

import com.tanaye.www.dto.PaysDTO;
import com.tanaye.www.entity.Pays;
import com.tanaye.www.service.PaysService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @Operation(summary = "Rechercher des pays par nom")
    public ResponseEntity<Page<Pays>> rechercher(@RequestParam(required = false) String nom,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(paysService.rechercher(nom, page, size));
    }
}

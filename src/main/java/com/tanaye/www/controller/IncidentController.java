package com.tanaye.www.controller;

import com.tanaye.www.dto.IncidentRechercheDTO;
import com.tanaye.www.dto.IncidentRequest;
import com.tanaye.www.entity.Incident;
import com.tanaye.www.enums.StatutIncident;
import com.tanaye.www.enums.TypeIncident;
import com.tanaye.www.service.IncidentService;
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
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Incidents", description = "Signalement et suivi d'incidents (Pivoy médiateur, non transporteur)")
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    @Operation(summary = "Signaler un incident")
    public ResponseEntity<Incident> creer(@RequestBody @Valid IncidentRequest req) {
        Incident i = incidentService.creer(req.declencheurId(), req.colisId(), req.type(), req.description());
        return ResponseEntity.ok(i);
    }

    @GetMapping
    @Operation(summary = "Lister par type")
    public ResponseEntity<Page<Incident>> listerParType(@RequestParam TypeIncident type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(incidentService.listerParType(type, pageable));
    }

    @GetMapping("/statut")
    @Operation(summary = "Lister par statut")
    public ResponseEntity<Page<Incident>> listerParStatut(@RequestParam StatutIncident statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(incidentService.listerParStatut(statut, pageable));
    }

    @PostMapping("/recherche")
    @Operation(summary = "Recherche avancée d'incidents (type, statut, user, colis, dates)")
    public ResponseEntity<Page<Incident>> recherche(@RequestBody IncidentRechercheDTO dto) {
        Pageable pageable = PageRequest.of(dto.page() != null ? dto.page() : 0, dto.size() != null ? dto.size() : 12);
        return ResponseEntity.ok(incidentService.rechercher(dto.type(), dto.statut(), dto.declencheurId(),
                dto.colisId(), dto.debut(), dto.fin(), pageable));
    }

    @PostMapping(value = "/export/csv", produces = "text/csv;charset=UTF-8")
    @Operation(summary = "Exporter les incidents en CSV selon filtres")
    public ResponseEntity<String> exporterCsv(@RequestBody IncidentRechercheDTO dto) {
        String csv = incidentService.exporterCsv(dto.type(), dto.statut(), dto.declencheurId(), dto.colisId(),
                dto.debut(), dto.fin());
        String bomCsv = "\uFEFF" + csv; // BOM UTF-8 pour Excel
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=incidents.csv")
                .body(bomCsv);
    }
}

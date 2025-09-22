package com.tanaye.www.controller;

import com.tanaye.www.dto.UtilisateurDTO;
import com.tanaye.www.dto.UtilisateurRechercheDTO;
import com.tanaye.www.dto.UtilisateurStatutDTO;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<Utilisateur> creer(@RequestBody UtilisateurDTO dto) {
        return ResponseEntity.ok(utilisateurService.creer(dto.toEntity()));
    }

    @GetMapping
    @Operation(summary = "Lister les utilisateurs")
    public ResponseEntity<Page<Utilisateur>> lister(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(utilisateurService.lister(page, size));
    }

    @PostMapping("/recherche")
    @Operation(summary = "Rechercher des utilisateurs (terme/role/statut/vérifié)")
    public ResponseEntity<Page<Utilisateur>> rechercher(@RequestBody UtilisateurRechercheDTO dto) {
        int page = dto.page() != null ? dto.page() : 0;
        int size = dto.size() != null ? dto.size() : 12;
        return ResponseEntity.ok(
                utilisateurService.rechercher(dto.terme(), dto.role(), dto.statut(), dto.verifie(), page, size));
    }

    @PutMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut et la vérification d'un utilisateur")
    public ResponseEntity<Utilisateur> majStatut(@PathVariable("id") Long utilisateurId,
            @RequestBody UtilisateurStatutDTO dto) {
        return ResponseEntity.ok(utilisateurService.mettreAJourStatut(utilisateurId, dto.statut(), dto.estVerifie()));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Chercher un utilisateur par email")
    public ResponseEntity<Optional<Utilisateur>> parEmail(@PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.parEmail(email));
    }

    @GetMapping("/stats/count-role")
    @Operation(summary = "Compter les utilisateurs par rôle")
    public ResponseEntity<Long> countByRole(@RequestParam RoleUtilisateur role) {
        return ResponseEntity.ok(utilisateurService.compterParRole(role));
    }

    @GetMapping("/verifies")
    @Operation(summary = "Lister les utilisateurs vérifiés par rôle")
    public ResponseEntity<Page<Utilisateur>> verifiesParRole(@RequestParam RoleUtilisateur role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(utilisateurService.listerVerifiesParRole(role, page, size));
    }

    @GetMapping("/verifies/count")
    @Operation(summary = "Compter les utilisateurs vérifiés")
    public ResponseEntity<Long> countVerifies() {
        return ResponseEntity.ok(utilisateurService.compterVerifies());
    }

    @GetMapping("/verifies/count-role")
    @Operation(summary = "Compter les utilisateurs vérifiés par rôle")
    public ResponseEntity<Long> countVerifiesParRole(@RequestParam RoleUtilisateur role) {
        return ResponseEntity.ok(utilisateurService.compterVerifiesParRole(role));
    }

    @GetMapping("/actifs-recents")
    @Operation(summary = "Lister les utilisateurs actifs récemment")
    public ResponseEntity<java.util.List<Utilisateur>> actifsRecents(@RequestParam(defaultValue = "30") int jours) {
        return ResponseEntity.ok(utilisateurService.actifsRecents(jours));
    }
}

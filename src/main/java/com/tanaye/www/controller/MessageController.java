package com.tanaye.www.controller;

import com.tanaye.www.dto.MessageDTO;
import com.tanaye.www.entity.Message;
import com.tanaye.www.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Messages", description = "Messagerie entre utilisateurs")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Envoyer un message")
    public ResponseEntity<Message> envoyer(@RequestBody MessageDTO dto) {
        return ResponseEntity.ok(messageService.envoyer(dto.auteurId(), dto.destinataireId(), dto.contenu()));
    }

    @GetMapping("/inbox/{destinataireId}")
    @Operation(summary = "Boîte de réception")
    public ResponseEntity<Page<Message>> inbox(@PathVariable Long destinataireId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.boiteReception(destinataireId, pageable));
    }

    @GetMapping("/outbox/{auteurId}")
    @Operation(summary = "Boîte d'envoi")
    public ResponseEntity<Page<Message>> outbox(@PathVariable Long auteurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.boiteEnvoi(auteurId, pageable));
    }
}

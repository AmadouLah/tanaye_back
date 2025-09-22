package com.tanaye.www.dto;

import com.tanaye.www.entity.Message;

public record MessageDTO(Long auteurId, Long destinataireId, String contenu) {
    public Message toEntity() {
        Message m = new Message();
        m.setContenu(contenu);
        return m;
    }
}

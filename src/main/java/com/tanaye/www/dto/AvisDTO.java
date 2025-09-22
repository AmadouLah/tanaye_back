package com.tanaye.www.dto;

import com.tanaye.www.entity.Avis;

public record AvisDTO(Long auteurId, Long destinataireId, Integer note, String commentaire) {
    public Avis toEntity() {
        Avis a = new Avis();
        a.setNote(note);
        a.setCommentaire(commentaire);
        return a;
    }
}

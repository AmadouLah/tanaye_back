package com.tanaye.www.dto;

import com.tanaye.www.enums.TypeNotification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationRechercheDTO {
    private Long userId;
    private String query;
    private TypeNotification type;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Boolean estLu;
}

package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutVoyage;
import lombok.Data;

@Data
public class VoyageStatistiquesDTO {
    private StatutVoyage statut;
    private long nombreVoyages;
    private double pourcentage;
}

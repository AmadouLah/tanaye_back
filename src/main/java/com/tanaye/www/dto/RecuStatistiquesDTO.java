package com.tanaye.www.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecuStatistiquesDTO {
    private int annee;
    private long nombreRecus;
    private BigDecimal montantTotal;
    private BigDecimal montantMoyen;
}

package com.tanaye.www.dto;

import com.tanaye.www.enums.TypeVille;
import lombok.Data;

@Data
public class VilleRechercheDTO {
    private String query;
    private Long regionId;
    private Long paysId;
    private String codePays;
    private String continent;
    private TypeVille type;
    private Boolean actif;
}

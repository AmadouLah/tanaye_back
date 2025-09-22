package com.tanaye.www.dto;

import com.tanaye.www.enums.Continent;
import lombok.Data;

@Data
public class PaysRechercheDTO {
    private String query;
    private Continent continent;
    private Boolean actif;
}

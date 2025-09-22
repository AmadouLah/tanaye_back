package com.tanaye.www.dto;

import com.tanaye.www.enums.StatutReclamation;
import com.tanaye.www.enums.TypeReclamation;
import lombok.Data;

@Data
public class ReclamationStatistiquesDTO {
    private String categorie; // statut ou type
    private String valeur; // valeur de la catégorie
    private long nombre;
}

package com.emploiapp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;


@Data
@Document(collection = "offres")
public class OffreEmploi {
    @Id
    private String id;

    @Indexed(unique = true)
    private String origineId;

    private String intitule;
    private String description;
    private String urlPostulation;
    private String entreprise;
    private String origineOffre;
    private String typeContrat;
    private String lieuTravail;
    private String pays;
    private LocalDateTime dateCreation;
    private LocalDateTime dateActualisation;
    private String ville;

    public OffreEmploi() {
        this.dateCreation = LocalDateTime.now();
        this.dateActualisation = LocalDateTime.now();
    }

}
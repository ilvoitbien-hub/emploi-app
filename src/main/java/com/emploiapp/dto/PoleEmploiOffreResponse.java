package com.emploiapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class PoleEmploiOffreResponse {

    // Getters and Setters
    @JsonProperty("resultats")
    private List<Resultat> resultats;

    @Setter
    @Getter
    public static class Resultat {

        // Getters and Setters
        @JsonProperty("id")
        private String id;

        @JsonProperty("intitule")
        private String intitule;

        @JsonProperty("description")
        private String description;

        @JsonProperty("urlPostulation")
        private String urlPostulation;

        @JsonProperty("entreprise")
        private Entreprise entreprise;

        @JsonProperty("lieuTravail")
        private LieuTravail lieuTravail;

        @JsonProperty("typeContrat")
        private String typeContrat;

        @JsonProperty("origineOffre")
        private OrigineOffre  origineOffre;

    }

    @Setter
    @Getter
    public static class Entreprise {

        // Getters and Setters
        @JsonProperty("nom")
        private String nom;

    }
    @Setter
    @Getter
    public static class OrigineOffre {

        // Getters and Setters
        @JsonProperty("urlOrigine")
        private String urlOrigine;

    }

    @Setter
    @Getter
    public static class LieuTravail {

        // Getters and Setters
        @JsonProperty("libelle")
        private String libelle;

    }
}
package com.emploiapp.controller;

import com.emploiapp.model.OffreEmploi;
import com.emploiapp.service.OffreEmploiService;
import com.emploiapp.service.StatistiqueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}) // Autorise Next.js
public class AccueilController {

    private final OffreEmploiService offreEmploiService;
    private final StatistiqueService statistiqueService;

    public AccueilController(OffreEmploiService offreEmploiService,
                             StatistiqueService statistiqueService) {
        this.offreEmploiService = offreEmploiService;
        this.statistiqueService = statistiqueService;
    }

    @GetMapping("/offres")
    public ResponseEntity<Map<String, Object>> getOffres(
            @RequestParam(value = "ville", required = false) String ville,
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "contrat", required = false) String typeContrat,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Récupérer toutes les offres
        Pageable pageable = PageRequest.of(page, size);
        Page<OffreEmploi> pageResult = offreEmploiService.findAll(pageable);

        List<OffreEmploi> offres = pageResult.getContent();

        // Appliquer les filtres sur les résultats de la page (ou sur toutes les offres si besoin)
        if (ville != null && !ville.isEmpty()) {
            offres = offres.stream()
                    .filter(o -> ville.equalsIgnoreCase(o.getVille()))
                    .collect(Collectors.toList());
        }

        if (searchTerm != null && !searchTerm.isEmpty()) {
            String finalSearchTerm = searchTerm.toLowerCase();
            offres = offres.stream()
                    .filter(o ->
                            (o.getIntitule() != null && o.getIntitule().toLowerCase().contains(finalSearchTerm)) ||
                                    (o.getDescription() != null && o.getDescription().toLowerCase().contains(finalSearchTerm)) ||
                                    (o.getEntreprise() != null && o.getEntreprise().toLowerCase().contains(finalSearchTerm))
                    )
                    .collect(Collectors.toList());
        }

        if (typeContrat != null && !typeContrat.isEmpty()) {
            offres = offres.stream()
                    .filter(o -> typeContrat.equalsIgnoreCase(o.getTypeContrat()))
                    .collect(Collectors.toList());
        }

        List<String> villesDisponibles = offreEmploiService.findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .map(OffreEmploi::getVille)
                .distinct()
                .collect(Collectors.toList());

        List<String> contratsDisponibles = offreEmploiService.findAll(Pageable.unpaged())
                .getContent()
                .stream()
                .map(OffreEmploi::getTypeContrat)
                .filter(contrat -> contrat != null && !contrat.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("offres", offres);
        response.put("stats", statistiqueService.getStatistiques());
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("villesDisponibles", villesDisponibles);
        response.put("contratsDisponibles", contratsDisponibles);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/offres/{id}")
    public ResponseEntity<OffreEmploi> getOffreById(@PathVariable String id) {
        return offreEmploiService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint supplémentaire pour les statistiques seules
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(statistiqueService.getStatistiques());
    }

    // Endpoint pour les villes disponibles
    @GetMapping("/villes")
    public ResponseEntity<List<String>> getVilles() {
        List<String> villes = offreEmploiService.findAll().stream()
                .map(OffreEmploi::getVille)
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(villes);
    }

    // Endpoint pour les types de contrat disponibles
    @GetMapping("/contrats")
    public ResponseEntity<List<String>> getContrats() {
        List<String> contrats = offreEmploiService.findAll().stream()
                .map(OffreEmploi::getTypeContrat)
                .filter(contrat -> contrat != null && !contrat.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        return ResponseEntity.ok(contrats);
    }
}
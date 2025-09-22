package com.emploiapp.controller;

import com.emploiapp.service.PoleEmploiService;
import com.emploiapp.service.StatistiqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final PoleEmploiService poleEmploiService;
    private final StatistiqueService statistiqueService;

    public ApiController(PoleEmploiService poleEmploiService,
                         StatistiqueService statistiqueService) {
        this.poleEmploiService = poleEmploiService;
        this.statistiqueService = statistiqueService;
    }

    @GetMapping("/test/auth")
    public ResponseEntity<String> testAuth() {
        poleEmploiService.testerConnexion();
        return ResponseEntity.ok("Test d'authentification effectué. Voir les logs pour les détails.");
    }

    @GetMapping("/test/offres")
    public ResponseEntity<String> testOffres() {
        poleEmploiService.recupererOffresParVilles();
        return ResponseEntity.ok("Test de récupération des offres effectué. Voir les logs pour les détails.");
    }

    @GetMapping("test/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(statistiqueService.getStatistiques());
    }

    @GetMapping("/update")
    public ResponseEntity<String> updateOffres() {
        try {
            poleEmploiService.recupererOffresParVilles();
            statistiqueService.afficherStatistiquesConsole();
            return ResponseEntity.ok("Mise à jour effectuée pour Rennes, Bordeaux et Paris");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }
}
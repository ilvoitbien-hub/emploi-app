package com.emploiapp.service;

import com.emploiapp.model.OffreEmploi;
import com.emploiapp.repository.OffreEmploiRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final OffreEmploiRepository offreEmploiRepository;

    public StatistiqueService(OffreEmploiRepository offreEmploiRepository) {
        this.offreEmploiRepository = offreEmploiRepository;
    }

    public Map<String, Object> getStatistiques() {
        List<OffreEmploi> offres = offreEmploiRepository.findAll();

        Map<String, Object> stats = new HashMap<>();

        // Statistiques par type de contrat
        Map<String, Long> statsContrat = offres.stream()
                .filter(o -> o.getTypeContrat() != null && !o.getTypeContrat().isEmpty())
                .collect(Collectors.groupingBy(OffreEmploi::getTypeContrat, Collectors.counting()));
        stats.put("parContrat", trierParValeurDesc(statsContrat));

        // Statistiques par entreprise
        Map<String, Long> statsEntreprise = offres.stream()
                .filter(o -> o.getEntreprise() != null && !o.getEntreprise().isEmpty())
                .collect(Collectors.groupingBy(OffreEmploi::getEntreprise, Collectors.counting()));
        stats.put("parEntreprise", trierParValeurDesc(statsEntreprise));

        // Statistiques par pays
        Map<String, Long> statsPays = offres.stream()
                .filter(o -> o.getPays() != null && !o.getPays().isEmpty())
                .collect(Collectors.groupingBy(OffreEmploi::getPays, Collectors.counting()));
        stats.put("parPays", trierParValeurDesc(statsPays));

        // Statistiques par ville
        Map<String, Long> statsVille = offres.stream()
                .filter(o -> o.getVille() != null && !o.getVille().isEmpty())
                .collect(Collectors.groupingBy(OffreEmploi::getVille, Collectors.counting()));
        stats.put("parVille", trierParValeurDesc(statsVille));

        stats.put("total", offres.size());
        stats.put("derniereMaj", new Date());

        return stats;
    }

    private Map<String, Long> trierParValeurDesc(Map<String, Long> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void afficherStatistiquesConsole() {
        Map<String, Object> stats = getStatistiques();

        System.out.println("===== STATISTIQUES DES OFFRES =====");
        System.out.println("Total des offres: " + stats.get("total"));
        System.out.println();

        System.out.println("Par type de contrat:");
        ((Map<String, Long>) stats.get("parContrat")).forEach((contrat, count) ->
                System.out.println("  " + contrat + ": " + count));
        System.out.println();

        System.out.println("Par entreprise (top 10):");
        ((Map<String, Long>) stats.get("parEntreprise")).entrySet().stream()
                .limit(10)
                .forEach(entry ->
                        System.out.println("  " + entry.getKey() + ": " + entry.getValue()));
        System.out.println();

        System.out.println("Par ville:");
        ((Map<String, Long>) stats.get("parVille")).forEach((ville, count) ->
                System.out.println("  " + ville + ": " + count));
        System.out.println();

        System.out.println("Par pays:");
        ((Map<String, Long>) stats.get("parPays")).forEach((pays, count) ->
                System.out.println("  " + pays + ": " + count));
    }
}
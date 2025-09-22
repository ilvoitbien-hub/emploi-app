package com.emploiapp.scheduler;

import com.emploiapp.service.PoleEmploiService;
import com.emploiapp.service.StatistiqueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OffreScheduler {

    private final PoleEmploiService poleEmploiService;
    private final StatistiqueService statistiqueService;

    public OffreScheduler(PoleEmploiService poleEmploiService,
                          StatistiqueService statistiqueService) {
        this.poleEmploiService = poleEmploiService;
        this.statistiqueService = statistiqueService;
    }

    @Scheduled(cron = "0 0 8 * * *") // Tous les jours à 8h
    public void mettreAJourOffresQuotidienne() {
        System.out.println("🔄 Début de la mise à jour quotidienne des offres...");
        poleEmploiService.recupererOffresParVilles();
        statistiqueService.afficherStatistiquesConsole();
        System.out.println("✅ Mise à jour quotidienne terminée.");
    }

    @Scheduled(fixedRate = 3600000) // Toutes les heures
    public void mettreAJourOffresHoraire() {
        System.out.println("🔄 Mise à jour horaire des offres...");
        poleEmploiService.recupererOffresParVilles();
    }
}
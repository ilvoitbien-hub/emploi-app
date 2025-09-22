package com.emploiapp.service;

import com.emploiapp.dto.PoleEmploiAuthResponse;
import com.emploiapp.dto.PoleEmploiOffreResponse;
import com.emploiapp.model.OffreEmploi;
import com.emploiapp.repository.OffreEmploiRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PoleEmploiService {

    private final OffreEmploiRepository offreEmploiRepository;
    @Value("${pole-emploi.auth.url}")
    private String authUrl;

    @Value("${pole-emploi.api.url}")
    private String apiUrl;

    @Value("${pole-emploi.client.id}")
    private String clientId;

    @Value("${pole-emploi.client.secret}")
    private String clientSecret;

    @Value("${pole-emploi.scope}")
    private String scope;

    @Value("${pole-emploi.ville.rennes}")
    private String codeInseeRennes;

    @Value("${pole-emploi.ville.bordeaux}")
    private String codeInseeBordeaux;

    @Value("${pole-emploi.ville.paris}")
    private String codeInseeParis;

    private final RestTemplate restTemplate;
    private final OffreEmploiService offreEmploiService;
    private String accessToken;
    private LocalDateTime tokenExpiration;

    public PoleEmploiService(OffreEmploiService offreEmploiService, OffreEmploiRepository offreEmploiRepository) {
        this.restTemplate = new RestTemplate();
        this.offreEmploiService = offreEmploiService;
        this.offreEmploiRepository = offreEmploiRepository;
    }

    public String obtenirToken() {
        if (accessToken != null && tokenExpiration != null &&
                LocalDateTime.now().isBefore(tokenExpiration)) {
            return accessToken;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            headers.set("Authorization", "Basic " + encodedCredentials);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("scope", scope);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            System.out.println("🔑 Tentative d'authentification auprès de France Travail...");

            ResponseEntity<PoleEmploiAuthResponse> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    request,
                    PoleEmploiAuthResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                accessToken = response.getBody().getAccessToken();
                tokenExpiration = LocalDateTime.now().plusSeconds(response.getBody().getExpiresIn() - 60);
                System.out.println("✅ Token obtenu avec succès. Expire dans " + response.getBody().getExpiresIn() + " secondes");
                return accessToken;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'obtention du token: " + e.getMessage());
        }

        return null;
    }

    public void recupererOffresParVilles() {
        System.out.println(" Récupération des offres pour Rennes, Bordeaux et Paris");

        Map<String, String> villes = new HashMap<>();
        villes.put("Rennes", codeInseeRennes);
        villes.put("Bordeaux", codeInseeBordeaux);
        villes.put("Paris", codeInseeParis);

        for (Map.Entry<String, String> entry : villes.entrySet()) {
            recupererOffresParVille(entry.getKey(), entry.getValue());
        }
    }
    private void analyserContentRange(String contentRange, String ville) {
        if (contentRange != null && contentRange.contains("/")) {
            try {
                // Format: "offres 0-49/125" ou "offres */0"
                String[] parties = contentRange.split(" ");
                if (parties.length >= 2) {
                    String[] plageEtTotal = parties[1].split("/");
                    if (plageEtTotal.length == 2) {
                        String[] debutFin = plageEtTotal[0].split("-");

                        if (debutFin.length == 2) {
                            int debut = Integer.parseInt(debutFin[0]);
                            int fin = Integer.parseInt(debutFin[1]);
                            int total = plageEtTotal[1].equals("*") ? -1 : Integer.parseInt(plageEtTotal[1]);

                            System.out.println("📈 " + ville + ": Pagination " + debut + "-" + fin + " sur " +
                                    (total == -1 ? "inconnu" : total) + " offres totales");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("⚠️  Erreur parsing Content-Range: " + contentRange);
            }
        }
    }
    public void recupererOffresParVille(String nomVille, String codeInsee) {
        try {
            String token = obtenirToken();

            if (token != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.set("Content-Type", "application/json");
                HttpEntity<String> entity = new HttpEntity<>(headers);


                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                        .queryParam("commune", codeInsee)
                        .queryParam("range", "0-49")  // Seulement 10 résultats pour tester
                        .queryParam("sort", "1")
                        .queryParam("distance", "0");

                String url = builder.toUriString();
                System.out.println("🌐 Requête pour " + nomVille + " (" + codeInsee + "): " + url);

                ResponseEntity<PoleEmploiOffreResponse> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        PoleEmploiOffreResponse.class);

                // Les statuts 200 et 206 sont tous deux des succès
                if ((response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.PARTIAL_CONTENT)
                        && response.getBody() != null) {

                    // Analyse de la pagination
                    String contentRange = response.getHeaders().getFirst("Content-Range");

                    analyserContentRange(contentRange, nomVille);

                    // Traitement des résultats
                    traiterReponse(response.getBody(), nomVille);
                    System.out.println("✅ " + nomVille + ": " +
                            response.getBody().getResultats().size() + " offres récupérées");

                } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                    System.out.println("ℹ️  " + nomVille + ": Aucune offre correspondante");
                } else {
                    System.out.println("❌ " + nomVille + ": Erreur " + response.getStatusCode());
                    System.out.println("Body: " + response.getBody());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la récupération des offres pour " + nomVille + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void traiterReponse(PoleEmploiOffreResponse response, String ville) {
        if (response.getResultats() != null) {
            for (PoleEmploiOffreResponse.Resultat offreData : response.getResultats()) {
                OffreEmploi offre = convertirEnOffreEmploi(offreData, ville);
                offreEmploiService.sauvegarderSiNouvelle(offre);
            }
        }
    }

    private OffreEmploi convertirEnOffreEmploi(PoleEmploiOffreResponse.Resultat data, String ville) {
        OffreEmploi offre = new OffreEmploi();
        offre.setOrigineId(data.getId());
        offre.setIntitule(data.getIntitule());
        offre.setDescription(data.getDescription());
        offre.setUrlPostulation(data.getUrlPostulation());

        if (data.getOrigineOffre() != null) {
            offre.setOrigineOffre(data.getOrigineOffre().getUrlOrigine());
        }

        if (data.getEntreprise() != null) {
            offre.setEntreprise(data.getEntreprise().getNom());
        }

        if (data.getLieuTravail() != null) {
            offre.setLieuTravail(data.getLieuTravail().getLibelle());
        }

        offre.setTypeContrat(data.getTypeContrat());
        offre.setVille(ville);
        offre.setPays("France");

        return offre;
    }
    public Optional<OffreEmploi> findById(Long id) {
        return offreEmploiRepository.findById(String.valueOf(id));
    }
    public void testerConnexion() {
        System.out.println("=== Test de connexion à l'API France Travail ===");
        System.out.println("Client ID: " + clientId);
        System.out.println("URL Auth: " + authUrl);
        System.out.println("URL API: " + apiUrl);
        System.out.println("Scope: " + scope);

        String token = obtenirToken();
        if (token != null) {
            System.out.println("✅ Token obtenu avec succès!");
            System.out.println("Token: " + token.substring(0, 50) + "...");
        } else {
            System.out.println("❌ Échec de l'obtention du token");
        }
    }
}
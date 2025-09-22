package com.emploiapp.service;

import com.emploiapp.model.OffreEmploi;
import com.emploiapp.repository.OffreEmploiRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OffreEmploiService {

    private final OffreEmploiRepository offreEmploiRepository;

    public OffreEmploiService(OffreEmploiRepository offreEmploiRepository) {
        this.offreEmploiRepository = offreEmploiRepository;
    }

    public List<OffreEmploi> findAll() {
        return offreEmploiRepository.findAll();
    }
    public Page<OffreEmploi> findAll(Pageable pageable) {
        return offreEmploiRepository.findAll(pageable);
    }

    public void sauvegarderSiNouvelle(OffreEmploi offre) {
        if (!offreEmploiRepository.existsByOrigineId(offre.getOrigineId())) {
            offreEmploiRepository.save(offre);
        }
    }

    public long count() {
        return offreEmploiRepository.count();
    }

    public Optional<OffreEmploi> findById(String id) {
        return offreEmploiRepository.findById(id);
    }
}
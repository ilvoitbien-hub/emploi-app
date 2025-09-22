package com.emploiapp.repository;

import com.emploiapp.model.OffreEmploi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OffreEmploiRepository extends MongoRepository<OffreEmploi, String> {
    Optional<OffreEmploi> findById(String id);
    Page<OffreEmploi> findAll(Pageable pageable);
    boolean existsByOrigineId(String origineId);
}
package com.delivery.repository;

import com.delivery.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    Optional<Vehicule> findByLivreurId(Long livreurId);
    boolean existsByImmatriculation(String immatriculation);
    boolean existsByLivreurId(Long livreurId);
}
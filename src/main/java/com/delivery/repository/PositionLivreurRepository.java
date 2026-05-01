package com.delivery.repository;

import com.delivery.entity.PositionLivreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PositionLivreurRepository extends JpaRepository<PositionLivreur, Long> {

    @Query("SELECT p FROM PositionLivreur p WHERE p.livreur.id = :livreurId ORDER BY p.timestamp DESC LIMIT 1")
    Optional<PositionLivreur> findDernierePositionByLivreurId(@Param("livreurId") Long livreurId);
}

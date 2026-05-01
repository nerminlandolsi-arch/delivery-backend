package com.delivery.repository;

import com.delivery.entity.Colis;
import com.delivery.enums.ColisStatus;
import com.delivery.enums.Priorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColisRepository extends JpaRepository<Colis, Long> {

    // ===== RECHERCHE =====
    Optional<Colis> findByCodeBarres(String codeBarres);
    Optional<Colis> findByNumeroSuivi(String numeroSuivi);

    // ===== LISTES =====
    List<Colis> findByLivreurId(Long livreurId);
    List<Colis> findByLivreurIdAndStatus(Long livreurId, ColisStatus status);
    List<Colis> findByStatus(ColisStatus status);

    // ===== COMPTAGE GENERAL =====
    long countByStatus(ColisStatus status);
    long countByLivreurId(Long livreurId);
    long countByLivreurIdAndStatus(Long livreurId, ColisStatus status);

    // ===== QUERIES EXISTANTES =====
    @Query("SELECT c FROM Colis c WHERE c.livreur.id = :livreurId " +
            "AND c.status NOT IN ('LIVRE', 'RETOURNE')")
    List<Colis> findColisEnCoursForLivreur(@Param("livreurId") Long livreurId);

    @Query("SELECT COUNT(c) FROM Colis c " +
            "WHERE c.createdAt BETWEEN :debut AND :fin")
    long countByPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(c) FROM Colis c " +
            "WHERE c.status = 'LIVRE' " +
            "AND c.dateLivraisonReelle BETWEEN :debut AND :fin")
    long countLivresParPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT c.livreur.id, COUNT(c) FROM Colis c " +
            "WHERE c.status = 'LIVRE' " +
            "GROUP BY c.livreur.id " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> findTopLivreurs();

    // ===== CHATBOT — QUESTION 1 =====
    long countByLivreurIdAndCreatedAtBetween(
            Long livreurId,
            LocalDateTime debut,
            LocalDateTime fin);

    // ===== CHATBOT — QUESTION 2 ET 5 =====
    long countByLivreurIdAndStatusAndUpdatedAtBetween(
            Long livreurId,
            ColisStatus status,
            LocalDateTime debut,
            LocalDateTime fin);

    List<Colis> findByLivreurIdAndStatusAndUpdatedAtBetween(
            Long livreurId,
            ColisStatus status,
            LocalDateTime debut,
            LocalDateTime fin);

    // ===== CHATBOT — QUESTION 3 =====
    List<Colis> findByLivreurIdAndPrioriteAndStatusOrderByCreatedAtAsc(
            Long livreurId,
            Priorite priorite,
            ColisStatus status);

    // ===== CHATBOT — QUESTION 6 ET 7 =====
    @Query("SELECT c.livreur.id, COUNT(c) FROM Colis c " +
            "WHERE c.status = 'LIVRE' " +
            "GROUP BY c.livreur.id " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> getClassementLivreurs();
}
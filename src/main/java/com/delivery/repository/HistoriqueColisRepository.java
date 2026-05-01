package com.delivery.repository;

import com.delivery.entity.HistoriqueColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriqueColisRepository extends JpaRepository<HistoriqueColis, Long> {
    List<HistoriqueColis> findByColisIdOrderByCreatedAtAsc(Long colisId);
}

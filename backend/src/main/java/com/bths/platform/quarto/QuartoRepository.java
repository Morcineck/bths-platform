package com.bths.platform.quarto;

import com.bths.platform.quarto.enums.StatusQuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByViagemId(Long viagemId);

    @Query("""
            SELECT COALESCE(SUM(q.capacidade), 0)
            FROM Quarto q
            WHERE q.viagem.id = :viagemId
            AND q.status <> :status
            """)
    long somarCapacidadePorViagemExcluindoStatus(
            @Param("viagemId") Long viagemId,
            @Param("status") StatusQuarto status
    );

}

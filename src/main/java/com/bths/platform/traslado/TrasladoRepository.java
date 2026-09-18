package com.bths.platform.traslado;

import com.bths.platform.traslado.enums.StatusTraslado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TrasladoRepository  extends JpaRepository<Traslado, Long> {

    List<Traslado> findByViagemId(Long viagemId);

    List<Traslado> findByHospedeId(Long hospedeId);

    long countByViagemIdAndStatus(
            long viagemId,
            StatusTraslado status
    );

    @Query("""
        SELECT t
        FROM Traslado t
        WHERE t.viagem.id = :viagemId
        AND t.status = :status
        AND t.dataHoraPrevista >= :agora
        ORDER BY t.dataHoraPrevista ASC
        """)
    List<Traslado> buscarProximosTraslados(
            @Param("viagemId") Long viagemId,
            @Param("status") StatusTraslado status,
            @Param("agora") LocalDateTime agora,
            Pageable pageable
    );

}

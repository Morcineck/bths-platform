package com.bths.platform.hospede;

import com.bths.platform.hospede.enums.StatusCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {

    boolean existsByCpfAndViagemId(String cpf, Long viagemId);

    boolean existsByCpfAndViagemIdAndIdNot(
            String cpf,
            Long viagemId,
            Long id
    );

    long countByViagemId(Long viagemId);

    @Query("""
            SELECT COUNT(h)
            FROM Hospede h
            WHERE h.viagem.id = :viagemId
            AND h.statusCheckIn = :statusCheckIn
            """)
    long contarPorViagemEStatusCheckIn(
            @Param("viagemId") Long viagemId,
            @Param("statusCheckIn") StatusCheckIn statusCheckIn
    );

    @Query("""
            SELECT h
            FROM Hospede h
            WHERE h.codigoCheckIn = :codigoCheckIn
            """)
    Optional<Hospede> findByCodigoCheckIn(
            @Param("codigoCheckIn") String codigoCheckIn
    );
}
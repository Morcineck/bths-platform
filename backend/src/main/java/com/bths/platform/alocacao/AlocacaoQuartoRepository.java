package com.bths.platform.alocacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlocacaoQuartoRepository extends JpaRepository<AlocacaoQuarto, Long> {

    long countByQuartoId(Long quartoId);

    long countByViagemId(Long viagemId);

    boolean existsByHospedeIdAndViagemId(Long hospedeId, Long viagemId);

    List<AlocacaoQuarto> findByQuartoId(Long quartoId);

    Optional<AlocacaoQuarto> findByHospedeIdAndViagemId(
            Long hospedeId,
            Long viagemId
    );
}

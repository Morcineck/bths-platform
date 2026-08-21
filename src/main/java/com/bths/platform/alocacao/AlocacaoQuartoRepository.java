package com.bths.platform.alocacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlocacaoQuartoRepository extends JpaRepository<AlocacaoQuarto, Long> {

    long countByQuartoId(Long quartoId);

    boolean existsByHospedeIdAndViagemId(Long hospedeId, Long viagemId);

    List<AlocacaoQuarto> findByQuartoId(Long quartoId);
}

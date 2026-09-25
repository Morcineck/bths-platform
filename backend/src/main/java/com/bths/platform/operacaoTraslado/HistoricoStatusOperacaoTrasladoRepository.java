package com.bths.platform.operacaoTraslado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoStatusOperacaoTrasladoRepository
        extends JpaRepository<
        HistoricoStatusOperacaoTraslado, Long> {

    List<HistoricoStatusOperacaoTraslado>
    findByOperacaoTrasladoId(
            Long operacaoTrasladoId
    );
}

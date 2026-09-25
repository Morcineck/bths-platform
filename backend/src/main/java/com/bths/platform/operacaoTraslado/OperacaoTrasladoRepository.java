package com.bths.platform.operacaoTraslado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperacaoTrasladoRepository
        extends JpaRepository<OperacaoTraslado, Long> {

    List<OperacaoTraslado> findByViagemId(
            Long viagemId
    );
}

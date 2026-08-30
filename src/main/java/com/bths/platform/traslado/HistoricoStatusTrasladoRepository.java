package com.bths.platform.traslado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoStatusTrasladoRepository extends JpaRepository<HistoricoStatusTraslado, Long> {

    List<HistoricoStatusTraslado> findByTrasladoId(Long trasladoId);
 }

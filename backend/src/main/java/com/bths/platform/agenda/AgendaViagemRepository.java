package com.bths.platform.agenda;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaViagemRepository extends JpaRepository<AgendaViagem, Long> {

    List<AgendaViagem> findByViagemIdOrderByOrdemAscDataHoraInicioAsc(
            Long viagemId
    );

    List<AgendaViagem> findByViagemIdAndAtivoTrueAndVisivelHospedeTrueOrderByOrdemAscDataHoraInicioAsc(
            Long viagemId
    );
}
package com.bths.platform.traslado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrasladoRepository  extends JpaRepository<Traslado, Long> {

    List<Traslado> findByViagemId(Long viagemId);

    List<Traslado> findByHospedeId(Long hospedeId);

}

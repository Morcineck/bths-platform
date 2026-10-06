package com.bths.platform.aviso;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {

    List<Aviso> findByViagemIdOrderByDataPublicacaoDesc(Long viagemId);

    List<Aviso> findByViagemIdAndAtivoTrueOrderByDataPublicacaoDesc(Long viagemId);
}

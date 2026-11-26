package com.bths.platform.hospedagem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospedagemRepository extends JpaRepository<Hospedagem, Long> {

    List<Hospedagem> findByViagemId(
            Long viagemId
    );

}

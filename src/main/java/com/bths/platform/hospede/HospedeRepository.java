package com.bths.platform.hospede;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {

    boolean existsByCpfAndViagemId(String cpf, Long viagemId);


    boolean existsByCpfAndViagemIdAndIdNot(
            String cpf,
            Long viagemId,
            Long id);
}

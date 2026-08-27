package com.bths.platform.hospede;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HospedeRepository extends JpaRepository<Hospede, Long> {

    boolean existsByCpfAndViagemId(String cpf, Long viagemId);


    boolean existsByCpfAndViagemIdAndIdNot(
            String cpf,
            Long viagemId,
            Long id);

    @Query("""
            SELECT h
            FROM Hospede h
            WHERE h.codigoCheckIn = :codigoCheckIn
            """)
    Optional<Hospede> findByCodigoCheckIn(
            @Param("codigoCheckIn") String codigoCheckIn
    );

}


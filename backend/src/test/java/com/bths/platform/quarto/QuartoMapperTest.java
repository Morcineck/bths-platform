package com.bths.platform.quarto;

import com.bths.platform.hospedagem.Hospedagem;
import com.bths.platform.quarto.dto.QuartoResponse;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;
import com.bths.platform.quarto.mapper.QuartoMapper;
import com.bths.platform.viagem.Viagem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class QuartoMapperTest {

    private final QuartoMapper quartoMapper =
            new QuartoMapper();

    @Test
    void deveConverterQuartoComHospedagemParaResponse() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospedagem hospedagem =
                new Hospedagem();

        hospedagem.setId(10L);
        hospedagem.setNome(
                "Chácara Beat Trips"
        );
        hospedagem.setViagem(
                viagem
        );

        Quarto quarto =
                new Quarto();

        quarto.setId(100L);
        quarto.setNome("Suíte 01");
        quarto.setTipo(
                TipoQuarto.SUITE
        );
        quarto.setCapacidade(6);
        quarto.setStatus(
                StatusQuarto.DISPONIVEL
        );
        quarto.setViagem(
                viagem
        );
        quarto.setHospedagem(
                hospedagem
        );

        QuartoResponse response =
                quartoMapper.paraResponse(
                        quarto
                );

        assertEquals(
                100L,
                response.getId()
        );

        assertEquals(
                "Suíte 01",
                response.getNome()
        );

        assertEquals(
                TipoQuarto.SUITE,
                response.getTipo()
        );

        assertEquals(
                6,
                response.getCapacidade()
        );

        assertEquals(
                StatusQuarto.DISPONIVEL,
                response.getStatus()
        );

        assertEquals(
                1L,
                response.getViagemId()
        );

        assertEquals(
                "Tomorrowland Brasil 2027",
                response.getViagemNome()
        );

        assertEquals(
                10L,
                response.getHospedagemId()
        );

        assertEquals(
                "Chácara Beat Trips",
                response.getHospedagemNome()
        );
    }

    @Test
    void deveConverterQuartoSemHospedagemParaResponse() {

        Viagem viagem =
                new Viagem();

        viagem.setId(1L);
        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        Quarto quarto =
                new Quarto();

        quarto.setId(100L);
        quarto.setNome("Suíte 01");
        quarto.setTipo(
                TipoQuarto.SUITE
        );
        quarto.setCapacidade(6);
        quarto.setStatus(
                StatusQuarto.DISPONIVEL
        );
        quarto.setViagem(
                viagem
        );

        QuartoResponse response =
                quartoMapper.paraResponse(
                        quarto
                );

        assertEquals(
                100L,
                response.getId()
        );

        assertEquals(
                1L,
                response.getViagemId()
        );

        assertNull(
                response.getHospedagemId()
        );

        assertNull(
                response.getHospedagemNome()
        );
    }
}
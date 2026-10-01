package com.bths.platform.hospedagem.mapper;

import com.bths.platform.hospedagem.Hospedagem;
import com.bths.platform.hospedagem.dto.HospedagemRequest;
import com.bths.platform.hospedagem.dto.HospedagemResponse;
import org.springframework.stereotype.Component;

@Component
public class HospedagemMapper {

    public HospedagemResponse paraResponse(
            Hospedagem hospedagem
    ) {

        HospedagemResponse response =
                new HospedagemResponse();

        response.setId(
                hospedagem.getId()
        );

        response.setNome(
                hospedagem.getNome()
        );

        response.setEndereco(
                hospedagem.getEndereco()
        );

        response.setCidade(
                hospedagem.getCidade()
        );

        response.setEstado(
                hospedagem.getEstado()
        );

        response.setLocalizacaoUrl(
                hospedagem.getLocalizacaoUrl()
        );

        response.setImagemUrl(
                hospedagem.getImagemUrl()
        );

        response.setWifiNome(
                hospedagem.getWifiNome()
        );

        response.setWifiSenha(
                hospedagem.getWifiSenha()
        );

        response.setHorarioCheckIn(
                hospedagem.getHorarioCheckIn()
        );

        response.setHorarioCheckOut(
                hospedagem.getHorarioCheckOut()
        );

        response.setContatoNome(
                hospedagem.getContatoNome()
        );

        response.setContatoTelefone(
                hospedagem.getContatoTelefone()
        );

        response.setObservacaoPublica(
                hospedagem.getObservacaoPublica()
        );

        if (hospedagem.getViagem() != null) {

            response.setViagemId(
                    hospedagem
                            .getViagem()
                            .getId()
            );
        }

        return response;
    }

    public void atualizarEntidade(
            Hospedagem hospedagem,
            HospedagemRequest request

    ) {

        hospedagem.setNome(
                request.getNome()
        );

        hospedagem.setEndereco(
                request.getEndereco()
        );

        hospedagem.setCidade(
                request.getCidade()
        );

        hospedagem.setEstado(
                request.getEstado()
        );

        hospedagem.setLocalizacaoUrl(
                request.getLocalizacaoUrl()
        );

        hospedagem.setImagemUrl(
                request.getImagemUrl()
        );

        hospedagem.setWifiNome(
                request.getWifiNome()
        );

        hospedagem.setWifiSenha(
                request.getWifiSenha()
        );


        hospedagem.setContatoNome(
                request.getContatoNome()
        );

        hospedagem.setContatoTelefone(
                request.getContatoTelefone()
        );

        hospedagem.setObservacaoPublica(
                request.getObservacaoPublica()
        );
    }
}

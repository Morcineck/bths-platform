package com.bths.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class LimiteTentativasLogin {

    private static final int LIMITE_DE_REGISTROS_PARA_LIMPEZA = 10_000;

    private final LimiteLoginProperties propriedades;
    private final Clock relogio;
    private final ConcurrentHashMap<String, Registro> registros = new ConcurrentHashMap<>();

    @Autowired
    public LimiteTentativasLogin(LimiteLoginProperties propriedades) {
        this(propriedades, Clock.systemUTC());
    }

    LimiteTentativasLogin(LimiteLoginProperties propriedades, Clock relogio) {
        this.propriedades = propriedades;
        this.relogio = relogio;
    }

    public static String normalizarChave(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public Optional<Duration> tempoRestanteDeBloqueio(String chave) {
        Registro registro = registros.get(chave);
        if (registro == null) {
            return Optional.empty();
        }

        Instant agora = relogio.instant();
        if (agora.isBefore(registro.bloqueadoAte())) {
            return Optional.of(Duration.between(agora, registro.bloqueadoAte()));
        }
        return Optional.empty();
    }

    public Optional<Duration> registrarFalha(String chave) {
        Instant agora = relogio.instant();
        AtomicReference<Duration> bloqueioAplicado = new AtomicReference<>();

        registros.compute(chave, (k, atual) ->
                proximoEstado(atual, agora, bloqueioAplicado));

        if (registros.size() > LIMITE_DE_REGISTROS_PARA_LIMPEZA) {
            limparRegistrosAntigos(agora);
        }

        return Optional.ofNullable(bloqueioAplicado.get());
    }

    public void registrarSucesso(String chave) {
        registros.remove(chave);
    }

    private Registro proximoEstado(
            Registro atual,
            Instant agora,
            AtomicReference<Duration> bloqueioAplicado
    ) {
        Registro base = atual != null ? atual : new Registro(0, agora, agora, 0);

        if (agora.isBefore(base.bloqueadoAte())) {
            return base;
        }

        int bloqueios = base.bloqueios();
        if (bloqueios > 0
                && !agora.isBefore(base.bloqueadoAte().plus(propriedades.bloqueioMaximo()))) {
            bloqueios = 0;
        }

        int falhas = base.falhas();
        Instant inicioJanela = base.inicioJanela();
        if (!agora.isBefore(inicioJanela.plus(propriedades.janela()))) {
            falhas = 0;
            inicioJanela = agora;
        }

        falhas++;

        if (falhas >= propriedades.maximoFalhas()) {
            Duration duracao = duracaoDoBloqueio(bloqueios);
            Instant bloqueadoAte = agora.plus(duracao);
            bloqueioAplicado.set(duracao);
            return new Registro(0, bloqueadoAte, bloqueadoAte, bloqueios + 1);
        }

        return new Registro(falhas, inicioJanela, base.bloqueadoAte(), bloqueios);
    }

    private Duration duracaoDoBloqueio(int bloqueiosAnteriores) {
        Duration duracao = propriedades.bloqueioInicial();
        for (int i = 0; i < bloqueiosAnteriores
                && duracao.compareTo(propriedades.bloqueioMaximo()) < 0; i++) {
            duracao = duracao.multipliedBy(2);
        }
        return duracao.compareTo(propriedades.bloqueioMaximo()) > 0
                ? propriedades.bloqueioMaximo()
                : duracao;
    }

    private void limparRegistrosAntigos(Instant agora) {
        registros.values().removeIf(registro ->
                !agora.isBefore(registro.inicioJanela()
                        .plus(propriedades.janela())
                        .plus(propriedades.bloqueioMaximo())));
    }

    private record Registro(
            int falhas,
            Instant inicioJanela,
            Instant bloqueadoAte,
            int bloqueios
    ) {
    }
}
package com.bths.platform.observabilidade;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestIdFilterTest {

    private final RequestIdFilter filtro = new RequestIdFilter();

    private ListAppender<ILoggingEvent> appender;
    private Logger loggerDoFiltro;

    @BeforeEach
    void capturarLogs() {
        loggerDoFiltro = (Logger) LoggerFactory.getLogger(RequestIdFilter.class);
        appender = new ListAppender<>() {
            @Override
            protected void append(ILoggingEvent evento) {
                // O Logback lê o MDC de forma preguiçosa. Fixa o conteúdo agora,
                // antes de o filtro limpar o MDC.
                evento.prepareForDeferredProcessing();
                super.append(evento);
            }
        };
        appender.start();
        loggerDoFiltro.addAppender(appender);
    }

    @AfterEach
    void limpar() {
        loggerDoFiltro.detachAppender(appender);
        MDC.clear();
    }

    @Test
    void geraRequestIdQuandoHeaderAusente() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/viagens");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> mdcDuranteRequisicao = new AtomicReference<>();

        filtro.doFilter(request, response,
                (req, res) -> mdcDuranteRequisicao.set(MDC.get(RequestIdFilter.MDC_KEY)));

        String idNaResposta = response.getHeader(RequestIdFilter.HEADER);

        assertThat(idNaResposta).isNotBlank();
        assertThat(mdcDuranteRequisicao.get()).isEqualTo(idNaResposta);
        assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNull();
    }

    @Test
    void reaproveitaRequestIdValidoRecebido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/viagens");
        request.addHeader(RequestIdFilter.HEADER, "abc-123_XYZ");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, (req, res) -> { });

        assertThat(response.getHeader(RequestIdFilter.HEADER)).isEqualTo("abc-123_XYZ");
    }

    @Test
    void descartaRequestIdInvalidoEGeraNovo() throws Exception {
        List<String> invalidos = List.of(
                "com espaco",
                "<script>alert(1)</script>",
                "a".repeat(65),
                "linha\r\nInjetada"
        );

        for (String invalido : invalidos) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/viagens");
            request.addHeader(RequestIdFilter.HEADER, invalido);
            MockHttpServletResponse response = new MockHttpServletResponse();

            filtro.doFilter(request, response, (req, res) -> { });

            assertThat(response.getHeader(RequestIdFilter.HEADER))
                    .isNotBlank()
                    .isNotEqualTo(invalido);
        }
    }

    @Test
    void limpaMdcMesmoQuandoARequisicaoFalha() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/viagens");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> filtro.doFilter(request, response,
                (req, res) -> { throw new ServletException("falha simulada"); }))
                .isInstanceOf(ServletException.class);

        assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNull();
        assertThat(response.getHeader(RequestIdFilter.HEADER)).isNotBlank();
        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(appender.list.get(0).getFormattedMessage()).contains("-> 500");
    }

    @Test
    void registraUmaLinhaComMetodoCaminhoEStatus() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/viagens");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, (req, res) -> response.setStatus(401));

        assertThat(appender.list).hasSize(1);
        ILoggingEvent evento = appender.list.get(0);
        assertThat(evento.getLevel()).isEqualTo(Level.INFO);
        assertThat(evento.getFormattedMessage()).startsWith("GET /api/viagens -> 401 (");
        assertThat(evento.getMDCPropertyMap())
                .containsEntry(RequestIdFilter.MDC_KEY, response.getHeader(RequestIdFilter.HEADER));
    }

    @Test
    void naoExibeCodigoDoQrCodeNosLogs() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/api/check-in/qr/CODIGO-SECRETO-123/imagem");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, (req, res) -> { });

        assertThat(appender.list).hasSize(1);
        String mensagem = appender.list.get(0).getFormattedMessage();
        assertThat(mensagem).doesNotContain("CODIGO-SECRETO-123");
        assertThat(mensagem).contains("/api/check-in/qr/{codigo}/imagem");
    }

    @Test
    void caminhoSeguroPreservaRotasComuns() {
        assertThat(RequestIdFilter.caminhoSeguro("/api/viagens/1")).isEqualTo("/api/viagens/1");
        assertThat(RequestIdFilter.caminhoSeguro("/api/check-in/qr/ABC")).isEqualTo("/api/check-in/qr/{codigo}");
        assertThat(RequestIdFilter.caminhoSeguro("/api/app/check-in/qr")).isEqualTo("/api/app/check-in/qr");
    }

    @Test
    void naoRegistraLinhaParaOHealthCheck() throws Exception {
        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/actuator/health/readiness");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filtro.doFilter(request, response, (req, res) -> { });

        assertThat(appender.list).isEmpty();
        assertThat(response.getHeader(RequestIdFilter.HEADER)).isNotBlank();
    }
}
package com.bths.platform.observabilidade;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Atribui um identificador a cada requisição, disponibiliza no MDC dos logs,
 * devolve no header da resposta e registra uma linha por requisição.
 *
 * Executa antes da cadeia do Spring Security, para que respostas 401 e 403
 * também carreguem o identificador.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    private static final Pattern ID_VALIDO = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");
    private static final String PREFIXO_QR = "/api/check-in/qr/";
    private static final String PREFIXO_HEALTH = "/actuator/health";

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String requestId = resolverRequestId(request.getHeader(HEADER));

        MDC.put(MDC_KEY, requestId);
        response.setHeader(HEADER, requestId);

        long inicio = System.nanoTime();
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        try {
            chain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            registrar(request, status, inicio);
            MDC.remove(MDC_KEY);
        }
    }

    private static String resolverRequestId(String recebido) {
        if (recebido != null && ID_VALIDO.matcher(recebido).matches()) {
            return recebido;
        }
        return UUID.randomUUID().toString();
    }

    private static void registrar(HttpServletRequest request, int status, long inicio) {
        String uri = request.getRequestURI();

        if (uri.startsWith(PREFIXO_HEALTH)) {
            return;
        }

        long duracaoMs = (System.nanoTime() - inicio) / 1_000_000;
        String caminho = caminhoSeguro(uri);

        if (status >= 500) {
            log.error("{} {} -> {} ({} ms)", request.getMethod(), caminho, status, duracaoMs);
        } else {
            log.info("{} {} -> {} ({} ms)", request.getMethod(), caminho, status, duracaoMs);
        }
    }

    /**
     * O código de check-in do hóspede trafega no caminho de /api/check-in/qr/{codigo}.
     * Ele é um passe pessoal e não deve aparecer nos logs.
     */
    static String caminhoSeguro(String uri) {
        if (uri.startsWith(PREFIXO_QR)) {
            int fimDoCodigo = uri.indexOf('/', PREFIXO_QR.length());
            String resto = fimDoCodigo >= 0 ? uri.substring(fimDoCodigo) : "";
            return PREFIXO_QR + "{codigo}" + resto;
        }
        return uri;
    }
}
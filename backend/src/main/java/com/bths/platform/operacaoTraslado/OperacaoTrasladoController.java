package com.bths.platform.operacaoTraslado;


import com.bths.platform.operacaoTraslado.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Operações de traslado",
        description = "Gestão das operações reais de transporte, incluindo veículo, motorista, passageiros, status e histórico.")
@SecurityRequirement(name = "bthsCookieAuth")
@RestController
@RequestMapping("/api/traslados/operacoes")
public class OperacaoTrasladoController {

    private final OperacaoTrasladoService operacaoTrasladoService;

    public OperacaoTrasladoController(
            OperacaoTrasladoService operacaoTrasladoService
    ) {
        this.operacaoTrasladoService =
                operacaoTrasladoService;
    }

    @Operation(summary = "Criar operação de traslado",
            description = "Cadastra uma operação de transporte vinculada a uma viagem. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Operação criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Recurso associado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito com regras operacionais.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OperacaoTrasladoResponse> criarOperacao(
            @Valid @RequestBody OperacaoTrasladoRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.criarOperacao(
                        request
                );

        URI location = URI.create(
                "/api/traslados/operacoes/"
                        + response.getId()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Vincular traslado à operação",
            description = "Associa um traslado existente a uma operação de transporte. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Traslado vinculado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação ou traslado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Vínculo incompatível com as regras operacionais.", content = @Content)
    })
    @PatchMapping(
            "/{operacaoId}/traslados/{trasladoId}"
    )
    public ResponseEntity<OperacaoTrasladoResponse> vincularTraslado(
            @PathVariable Long operacaoId,
            @PathVariable Long trasladoId
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService
                        .vincularTraslado(
                                operacaoId,
                                trasladoId
                        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Alterar veículo da operação",
            description = "Substitui o veículo associado à operação, respeitando as regras de disponibilidade e capacidade. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veículo alterado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação ou veículo não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Veículo incompatível ou capacidade insuficiente.", content = @Content)
    })
    @PatchMapping(
            "/{operacaoId}/veiculo/{veiculoId}"
    )
    public ResponseEntity<OperacaoTrasladoResponse> alterarVeiculo(
            @PathVariable Long operacaoId,
            @PathVariable Long veiculoId
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.alterarVeiculo(
                        operacaoId,
                        veiculoId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar operações por viagem",
            description = "Consulta as operações de transporte vinculadas a uma viagem. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operações retornadas com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada.", content = @Content)
    })
    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<OperacaoTrasladoResponse>> listarPorViagem(
            @PathVariable Long viagemId
    ) {

        List<OperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarPorViagem(
                                viagemId
                        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar operação de traslado",
            description = "Atualiza os dados de uma operação de transporte existente. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação ou recurso associado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Atualização incompatível com as regras operacionais.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<OperacaoTrasladoResponse> atualizarOperacao(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoTrasladoUpdateRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.atualizarOperacao(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar operação de traslado",
            description = "Consulta os dados de uma operação de transporte pelo identificador. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação encontrada."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<OperacaoTrasladoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService
                        .buscarPorId(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Excluir operação de traslado",
            description = "Exclui uma operação de transporte, respeitando os vínculos de passageiros. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Operação excluída com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Exclusão impedida por passageiros vinculados.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOperacao(
            @PathVariable Long id
    ) {

        operacaoTrasladoService
                .excluirOperacao(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar passageiros da operação",
            description = "Retorna os passageiros vinculados à operação de traslado. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passageiros retornados com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content)
    })
    @GetMapping("/{id}/passageiros")
    public ResponseEntity<List<OperacaoTrasladoPassageiroResponse>>
    listarPassageiros(
            @PathVariable Long id
    ) {

        List<OperacaoTrasladoPassageiroResponse> passageiros =
                operacaoTrasladoService
                        .listarPassageiros(id);

        return ResponseEntity.ok(
                passageiros
        );
    }

    @Operation(summary = "Atualizar status da operação",
            description = "Altera o status operacional do transporte, respeitando as transições permitidas. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Transição de status não permitida.", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OperacaoTrasladoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoTrasladoStatusRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.atualizarStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar histórico de status da operação",
            description = "Retorna o histórico de alterações de status de uma operação de traslado. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content)
    })
    @GetMapping("/{id}/historico-status")
    public ResponseEntity<List<HistoricoStatusOperacaoTrasladoResponse>>
    listarHistoricoStatus(
            @PathVariable Long id
    ) {

        List<HistoricoStatusOperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarHistoricoStatus(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Corrigir status da operação",
            description = "Corrige o status de uma operação mediante justificativa, registrando a alteração no histórico. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status corrigido com sucesso."),
            @ApiResponse(responseCode = "400", description = "Correção inválida ou justificativa ausente.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Operação não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Correção incompatível com as regras de status.", content = @Content)
    })
    @PatchMapping("/{id}/corrigir-status")
    public ResponseEntity<OperacaoTrasladoResponse> corrigirStatus(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoTrasladoCorrecaoStatusRequest request

    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.corrigirStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }
}

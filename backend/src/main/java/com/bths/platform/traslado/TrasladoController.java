package com.bths.platform.traslado;

import com.bths.platform.traslado.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Traslados", description = "Gestão dos dados individuais de transporte dos hóspedes.")
@SecurityRequirement(name = "bthsCookieAuth")
@RestController
@RequestMapping("/api/traslados")
public class TrasladoController {

    private final TrasladoService trasladoService;

    public TrasladoController(TrasladoService trasladoService) {
        this.trasladoService = trasladoService;
    }

    @Operation(summary = "Cadastrar traslado", description = "Cadastra um traslado vinculado à operação de viagem. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Traslado cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede ou viagem não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito com regras de traslado.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TrasladoResponse> cadastrarTraslado(
            @Valid @RequestBody TrasladoRequest request
    ) {

        TrasladoResponse response =
                trasladoService.cadastrarTraslado(request);

        URI location = URI.create(
                "/api/traslados/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(summary = "Consultar traslado", description = "Consulta um traslado pelo identificador. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Traslado encontrado."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado não encontrado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrasladoResponse> buscarTraslado(
            @PathVariable Long id
    ) {

        TrasladoResponse response =
                trasladoService.buscarTrasladoPorId(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar traslados por viagem", description = "Retorna os traslados associados a uma viagem. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de traslados retornada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada.", content = @Content)
    })
    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<TrasladoResponse>> listarTrasladoPorViagem(
            @PathVariable Long viagemId
    ) {

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorViagem(viagemId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar traslados por hóspede", description = "Retorna os traslados vinculados a um hóspede. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de traslados retornada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @GetMapping("/hospede/{hospedeId}")
    public ResponseEntity<List<TrasladoResponse>> listarTrasladoPorHospede(
            @PathVariable Long hospedeId
    ) {

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorHospede(hospedeId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar traslado", description = "Atualiza as informações de um traslado e valida sua compatibilidade com a operação vinculada. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Traslado atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Incompatibilidade com a operação vinculada.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TrasladoResponse> atualizarTraslado(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoUpdateRequest request
    ) {

        TrasladoResponse response = trasladoService.atualizarTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar status do traslado", description = "Altera o status do traslado respeitando as transições permitidas. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Transição de status não permitida.", content = @Content)
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TrasladoResponse> atualizarStatusTraslado(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoStatusRequest request
    ) {

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Corrigir status do traslado",
            description = "Realiza uma correção de status com justificativa e registro de histórico. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status corrigido com sucesso."),
            @ApiResponse(responseCode = "400", description = "Correção inválida ou justificativa ausente.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Correção de status incompatível.", content = @Content)
    })
    @PatchMapping("/{id}/corrigir-status")
    public ResponseEntity<TrasladoResponse> corrigirStatusTraslado(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoCorrecaoStatusRequest request

    ) {

        TrasladoResponse response =
                trasladoService.corrigirStatusTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar histórico de status", description = "Lista as alterações de status registradas para um traslado. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado não encontrado.", content = @Content)
    })
    @GetMapping("/{id}/historico-status")
    public ResponseEntity<List<HistoricoStatusTrasladoResponse>> listarHistoricoStatusTraslado(
            @PathVariable Long id
    ) {

        List<HistoricoStatusTrasladoResponse> response =
                trasladoService.listarHistoricoStatusTraslado(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Associar operação ao traslado", description = "Associa motorista e veículo ao traslado, respeitando as validações operacionais. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação associada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Traslado, motorista ou veículo não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Motorista, veículo ou associação incompatível.", content = @Content)
    })
    @PatchMapping("/{id}/operacao")
    public ResponseEntity<TrasladoResponse> associarOperacao(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoOperacaoRequest request
    ) {

        TrasladoResponse response =
                trasladoService.associarOperacao(id, request);

        return ResponseEntity.ok(response);
    }

}

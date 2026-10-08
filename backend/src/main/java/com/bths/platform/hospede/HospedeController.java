package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Hóspedes", description = "Gestão administrativa e operacional dos hóspedes vinculados às viagens.")
@SecurityRequirement(name = "bthsCookieAuth")
@RestController
@RequestMapping("/api/hospedes")
public class HospedeController {

    private final HospedeService hospedeService;

    public HospedeController(HospedeService hospedeService) {
        this.hospedeService = hospedeService;
    }

    @Operation(summary = "Cadastrar hóspede", description = "Cadastra um hóspede. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hóspede cadastrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada.", content = @Content),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado nesta viagem.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<HospedeResponse> cadastrarHospede(
            @Valid @RequestBody HospedeRequest request
    ) {

        HospedeResponse response =
                hospedeService.cadastrarHospede(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Redefinir senha do hóspede", description = "Redefine a senha de acesso BTHS de um hóspede. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @PutMapping("/{id}/acesso-bths/senha")
    public ResponseEntity<Void> redefinirSenhaAcessoBths(
            @PathVariable Long id,
            @Valid @RequestBody HospedeRedefinirSenhaRequest request
    ) {

        hospedeService.redefinirSenhaAcessoBths(
                id,
                request
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(summary = "Listar hóspedes", description = "Lista os hóspedes cadastrados. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hóspedes retornados com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<HospedeResponse>> listarHospedes() {

        return ResponseEntity.ok
                (hospedeService.listarHospedes());
    }

    @Operation(summary = "Consultar hóspede", description = "Consulta os dados de um hóspede pelo identificador. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hóspede encontrado."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<HospedeResponse> buscarHospedePorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                hospedeService.buscarHospedePorId(id)
        );
    }

    @Operation(summary = "Atualizar hóspede", description = "Atualiza os dados cadastrais de um hóspede. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hóspede atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado nesta viagem.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<HospedeResponse> atualizarHospede(
            @PathVariable Long id,
            @Valid @RequestBody HospedeRequest request
    ) {

        return ResponseEntity.ok(
                hospedeService.atualizarHospede(id, request));
    }

    @Operation(summary = "Excluir hóspede", description = "Exclui um hóspede cadastrado. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Hóspede excluído com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHospede(
            @PathVariable Long id
    ) {
        hospedeService.deletarHospede(id);
        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Listar hóspedes por viagem", description = "Consulta os hóspedes vinculados a uma viagem específica. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de hóspedes retornada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada.", content = @Content)
    })
    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<HospedeResponse>> listarHospedesPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                hospedeService.listarHospedesPorViagem(viagemId)
        );
    }

    @Operation(summary = "Vincular conta ao hóspede", description = "Vincula uma conta de usuário existente a um hóspede. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta vinculada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede ou usuário não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito de vínculo.", content = @Content)
    })
    @PatchMapping("/{hospedeId}/usuario/{usuarioId}")
    public ResponseEntity<HospedeResponse> vincularUsuarioAoHospede(
            @PathVariable Long hospedeId,
            @PathVariable UUID usuarioId
    ) {

        HospedeResponse response =
                hospedeService.vincularUsuarioAoHospede(
                        hospedeId,
                        usuarioId
                );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar acesso BTHS", description = "Consulta os dados de acesso BTHS associados ao hóspede. Acesso exclusivo ao ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados de acesso retornados com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @GetMapping("/{id}/acesso-bths")
    public ResponseEntity<HospedeAcessoBthsResponse> buscarAcessoBths(
            @PathVariable Long id
    ) {

        HospedeAcessoBthsResponse response =
                hospedeService.buscarAcessoBths(
                        id
                );

        return ResponseEntity.ok(
                response
        );
    }

    @Operation(summary = "Criar ou vincular acesso BTHS", description = "Provisiona ou vincula uma conta de acesso BTHS ao hóspede. Acesso exclusivo ao ADMIN.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Acesso criado ou vinculado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso restrito ao ADMIN.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar ou vincular o acesso.", content = @Content)
    })
    @PostMapping("/{id}/acesso-bths")
    public ResponseEntity<HospedeAcessoBthsResponse> criarOuVincularAcessoBths(
            @PathVariable Long id,
            @Valid @RequestBody HospedeCriarAcessoBthsRequest request
    ) {

        HospedeAcessoBthsResponse response =
                hospedeService.criarOuVincularAcessoBths(
                        id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}

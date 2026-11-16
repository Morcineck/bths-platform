package com.bths.platform.usuario;

import com.bths.platform.usuario.dto.UsuarioAtualizarRequest;
import com.bths.platform.usuario.dto.UsuarioRequest;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.EmailUsuarioJaCadastradoException;
import com.bths.platform.usuario.exception.OperacaoUsuarioNaoPermitidaException;
import com.bths.platform.usuario.exception.PerfilUsuarioInvalidoException;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.usuario.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSenhaCriptografadaEAtivo() {

        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Administrador Beat Trips");
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");
        request.setPerfil(PerfilUsuario.ADMIN);

        UUID id = UUID.randomUUID();

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode("senha123"))
                .thenReturn("senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setId(id);
                    return usuario;
                });

        UsuarioResponse responseEsperado = new UsuarioResponse();
        responseEsperado.setId(id);
        responseEsperado.setNome("Administrador Beat Trips");
        responseEsperado.setPerfil(PerfilUsuario.ADMIN);
        responseEsperado.setAtivo(true);

        when(usuarioMapper.paraResponse(any(Usuario.class)))
                .thenReturn(responseEsperado);

        UsuarioResponse response =
                usuarioService.cadastrarUsuario(request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Administrador Beat Trips", response.getNome());
        assertEquals(PerfilUsuario.ADMIN, response.getPerfil());
        assertTrue(response.isAtivo());

        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getNome().equals("Administrador Beat Trips")
                        && usuario.getEmail().equals("admin@beattrips.com")
                        && usuario.getSenha().equals("senha-criptografada")
                        && usuario.getPerfil() == PerfilUsuario.ADMIN
                        && usuario.isAtivo()
        ));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {

        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Administrador Beat Trips");
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");
        request.setPerfil(PerfilUsuario.ADMIN);

        when(usuarioRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailUsuarioJaCadastradoException.class,
                () -> usuarioService.cadastrarUsuario(request)
        );

        verify(usuarioRepository).existsByEmail("admin@beattrips.com");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(usuarioRepository, never())
                .save(any(Usuario.class));

        verify(usuarioMapper, never())
                .paraResponse(any(Usuario.class));
    }

    @Test
    void deveBuscarUsuarioPorId() {

        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Administrador Beat Trips");
        usuario.setEmail("admin@beattrips.com");
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);

        UsuarioResponse responseEsperado = new UsuarioResponse();
        responseEsperado.setId(id);
        responseEsperado.setNome("Administrador Beat Trips");
        responseEsperado.setPerfil(PerfilUsuario.ADMIN);
        responseEsperado.setAtivo(true);

        when(usuarioRepository.findById(id))
                .thenReturn(java.util.Optional.of(usuario));

        when(usuarioMapper.paraResponse(usuario))
                .thenReturn(responseEsperado);

        UsuarioResponse response =
                usuarioService.buscarUsuarioPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Administrador Beat Trips", response.getNome());
        assertEquals(PerfilUsuario.ADMIN, response.getPerfil());
        assertTrue(response.isAtivo());

        verify(usuarioRepository).findById(id);
        verify(usuarioMapper).paraResponse(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarUsuarioPorId(id)
        );

        verify(usuarioRepository).findById(id);

        verify(usuarioMapper, never())
                .paraResponse(any(Usuario.class));
    }

    private Usuario criarUsuario(PerfilUsuario perfil, boolean ativo, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Usuário " + perfil);
        usuario.setEmail(email);
        usuario.setPerfil(perfil);
        usuario.setAtivo(ativo);
        return usuario;
    }

    @Test
    void deveListarSomenteUsuariosDaEquipe() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN, true, "admin@beattrips.com");
        Usuario staff = criarUsuario(PerfilUsuario.STAFF, true, "staff@beattrips.com");

        when(usuarioRepository.findByPerfilInOrderByNomeAsc(
                List.of(PerfilUsuario.ADMIN, PerfilUsuario.STAFF)))
                .thenReturn(List.of(admin, staff));
        when(usuarioMapper.paraResponse(any(Usuario.class)))
                .thenReturn(new UsuarioResponse());

        List<UsuarioResponse> equipe = usuarioService.listarEquipe();

        assertEquals(2, equipe.size());
        verify(usuarioRepository).findByPerfilInOrderByNomeAsc(
                List.of(PerfilUsuario.ADMIN, PerfilUsuario.STAFF));
    }

    @Test
    void deveRecusarCadastroComPerfilHospede() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Hóspede");
        request.setEmail("hospede@beattrips.com");
        request.setSenha("senha123");
        request.setPerfil(PerfilUsuario.HOSPEDE);

        assertThrows(PerfilUsuarioInvalidoException.class,
                () -> usuarioService.cadastrarUsuario(request));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUsuarioDaEquipe() {
        Usuario staff = criarUsuario(PerfilUsuario.STAFF, true, "staff@beattrips.com");

        UsuarioAtualizarRequest request = new UsuarioAtualizarRequest();
        request.setNome("Staff Editado");
        request.setEmail("novo@beattrips.com");
        request.setPerfil(PerfilUsuario.STAFF);

        when(usuarioRepository.findById(staff.getId())).thenReturn(Optional.of(staff));
        when(usuarioRepository.existsByEmailAndIdNot("novo@beattrips.com", staff.getId()))
                .thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));
        when(usuarioMapper.paraResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

        usuarioService.atualizarUsuario(staff.getId(), request);

        verify(usuarioRepository).save(argThat(u ->
                u.getNome().equals("Staff Editado")
                        && u.getEmail().equals("novo@beattrips.com")
                        && u.getPerfil() == PerfilUsuario.STAFF));
    }

    @Test
    void deveRecusarAtualizacaoComEmailDeOutroUsuario() {
        Usuario staff = criarUsuario(PerfilUsuario.STAFF, true, "staff@beattrips.com");

        UsuarioAtualizarRequest request = new UsuarioAtualizarRequest();
        request.setNome("Staff");
        request.setEmail("ocupado@beattrips.com");
        request.setPerfil(PerfilUsuario.STAFF);

        when(usuarioRepository.findById(staff.getId())).thenReturn(Optional.of(staff));
        when(usuarioRepository.existsByEmailAndIdNot("ocupado@beattrips.com", staff.getId()))
                .thenReturn(true);

        assertThrows(EmailUsuarioJaCadastradoException.class,
                () -> usuarioService.atualizarUsuario(staff.getId(), request));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveImpedirRebaixarUltimoAdminAtivo() {
        Usuario admin = criarUsuario(PerfilUsuario.ADMIN, true, "admin@beattrips.com");

        UsuarioAtualizarRequest request = new UsuarioAtualizarRequest();
        request.setNome("Admin");
        request.setEmail("admin@beattrips.com");
        request.setPerfil(PerfilUsuario.STAFF);

        when(usuarioRepository.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(usuarioRepository.existsByEmailAndIdNot("admin@beattrips.com", admin.getId()))
                .thenReturn(false);
        when(usuarioRepository.countByPerfilAndAtivoTrue(PerfilUsuario.ADMIN)).thenReturn(1L);

        assertThrows(OperacaoUsuarioNaoPermitidaException.class,
                () -> usuarioService.atualizarUsuario(admin.getId(), request));

        verify(usuarioRepository, never()).save(any());
    }

}
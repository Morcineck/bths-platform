package com.bths.platform.usuario;

import com.bths.platform.usuario.exception.EmailUsuarioJaCadastradoException;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.usuario.dto.UsuarioRequest;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
package com.proyect.user.Service;

import com.proyect.user.Model.Rol;
import com.proyect.user.Model.Usuario;
import com.proyect.user.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword("hashedPassword");
    }

    @Test
    void login_Exitoso() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        Usuario resultado = usuarioService.login("test@test.com", "password123");

        assertNotNull(resultado);
        assertEquals("test@test.com", resultado.getEmail());
    }

    @Test
    void login_FallaPorPasswordIncorrecto() {
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        Usuario resultado = usuarioService.login("test@test.com", "wrongPassword");

        assertNull(resultado);
    }

    @SuppressWarnings("null")
    @Test
    void save_EncriptaPasswordYActivaUsuario() {
        Usuario usuarioParaGuardar = new Usuario();
        usuarioParaGuardar.setEmail("nuevo@correo.com");
        usuarioParaGuardar.setPassword("clavePlana");

        when(passwordEncoder.encode("clavePlana")).thenReturn("claveEncriptada123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioParaGuardar);

        Usuario resultado = usuarioService.save(usuarioParaGuardar);

        // 4. Verificamos que tu lógica de negocio funcionó perfectamente
        assertTrue(resultado.getActive(), "El usuario debería nacer activo por defecto");
        assertEquals("claveEncriptada123", resultado.getPassword(), "La contraseña debió ser encriptada antes de guardar");
        
        verify(usuarioRepository, times(1)).save(usuarioParaGuardar);
    }

    @Test
    void delete_CuandoElUsuarioExiste_RetornaTrue() {
        when(usuarioRepository.existsById("123")).thenReturn(true);

        boolean resultado = usuarioService.delete("123");

        assertTrue(resultado);
        verify(usuarioRepository, times(1)).deleteById("123");
    }

    @SuppressWarnings("null")
    @Test
    void delete_CuandoElUsuarioNoExiste_RetornaFalse() {
        when(usuarioRepository.existsById("999")).thenReturn(false);

        boolean resultado = usuarioService.delete("999");

        assertFalse(resultado);
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    @SuppressWarnings("null")
    @Test
    void update_MantieneLaPasswordIntacta() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId("1");
        usuarioExistente.setName("Nombre Viejo");
        usuarioExistente.setPassword("passwordIntacta");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setName("Nombre Nuevo");
        usuarioActualizado.setRole(Rol.ROLE_ADMIN);

        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.update("1", usuarioActualizado);

        assertEquals("Nombre Nuevo", resultado.getName());
        assertEquals("passwordIntacta", resultado.getPassword(), "La contraseña no debe sobreescribirse en el update");
    }
}
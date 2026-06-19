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
        // Simulamos que el repositorio encuentra al usuario
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));
        // Simulamos que la contraseña coincide
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
        // 1. Preparamos los datos (Lo que enviaría React)
        Usuario usuarioParaGuardar = new Usuario();
        usuarioParaGuardar.setEmail("nuevo@correo.com");
        usuarioParaGuardar.setPassword("clavePlana");

        // 2. Le decimos a Mockito qué hacer cuando se llamen a las herramientas
        // Simulamos que el encoder hace su trabajo
        when(passwordEncoder.encode("clavePlana")).thenReturn("claveEncriptada123");
        // Simulamos que el repositorio devuelve el mismo usuario al guardarlo
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioParaGuardar);

        // 3. Ejecutamos el método real de tu servicio
        Usuario resultado = usuarioService.save(usuarioParaGuardar);

        // 4. Verificamos que tu lógica de negocio funcionó perfectamente
        assertTrue(resultado.isActive(), "El usuario debería nacer activo por defecto");
        assertEquals("claveEncriptada123", resultado.getPassword(), "La contraseña debió ser encriptada antes de guardar");
        
        // Verificamos que efectivamente se llamó al método save del repositorio 1 vez
        verify(usuarioRepository, times(1)).save(usuarioParaGuardar);
    }

    @Test
    void delete_CuandoElUsuarioExiste_RetornaTrue() {
        // Simulamos que la base de datos dice "Sí, este ID existe"
        when(usuarioRepository.existsById("123")).thenReturn(true);

        // Ejecutamos
        boolean resultado = usuarioService.delete("123");

        // Verificamos que devuelve true y que sí llamó a la acción de borrar
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).deleteById("123");
    }

    @SuppressWarnings("null")
    @Test
    void delete_CuandoElUsuarioNoExiste_RetornaFalse() {
        // Simulamos que la base de datos dice "No conozco este ID"
        when(usuarioRepository.existsById("999")).thenReturn(false);

        // Ejecutamos
        boolean resultado = usuarioService.delete("999");

        // Verificamos que devuelve false y que NUNCA intentó borrar nada
        assertFalse(resultado);
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    @SuppressWarnings("null")
    @Test
    void update_MantieneLaPasswordIntacta() {
        // Preparamos un usuario existente simulado en la BD
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId("1");
        usuarioExistente.setName("Nombre Viejo");
        usuarioExistente.setPassword("passwordIntacta");

        // Preparamos la actualización que viene del controlador
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setName("Nombre Nuevo");
        usuarioActualizado.setRole(Rol.ROLE_ADMIN);

        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.update("1", usuarioActualizado);

        // Verificamos que los campos se actualizaron, pero la contraseña no se tocó
        assertEquals("Nombre Nuevo", resultado.getName());
        assertEquals("passwordIntacta", resultado.getPassword(), "La contraseña no debe sobreescribirse en el update");
    }
}
package com.proyect.user.Service;

import com.proyect.user.Model.Usuario;
import com.proyect.user.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // NUEVO: Importación del encriptador
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // NUEVO: Inyección de la herramienta BCrypt

    //Todos los usuarios
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    //Por Id
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    //Registrar
    public Usuario save(Usuario usuario) {
        usuario.setActive(true);
        
        // MODIFICADO: Encriptamos la contraseña antes de mandarla a MongoDB
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        
        return usuarioRepository.save(usuario);
    }

    //Actualizar
    public Usuario update(String id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setName(usuarioActualizado.getName());
            usuario.setPhone(usuarioActualizado.getPhone());
            usuario.setRole(usuarioActualizado.getRole());
            usuario.setPetsIds(usuarioActualizado.getPetsIds());
            usuario.setActive(usuarioActualizado.isActive());
            // Mantengo tu lógica intacta: el update no altera la contraseña
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    //Eliminar
    public boolean delete(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //Login
    public Usuario login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        // MODIFICADO: Cambiamos .equals() por .matches() para que compare de forma segura el hash
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario.get();
        }
        return null;
    }
}
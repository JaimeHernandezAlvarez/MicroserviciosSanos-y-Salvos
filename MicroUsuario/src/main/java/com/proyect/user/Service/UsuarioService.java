package com.proyect.user.Service;

import com.proyect.user.Model.Usuario;
import com.proyect.user.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("null")
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        // Validamos si el usuario existe y si la contraseña coincide (Texto plano por ahora)
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return usuario.get();
        }
        return null;
    }
}
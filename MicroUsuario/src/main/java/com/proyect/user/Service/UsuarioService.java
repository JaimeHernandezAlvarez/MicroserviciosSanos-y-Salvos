package com.proyect.user.Service;

import com.proyect.user.Model.Usuario;
import com.proyect.user.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        usuario.setActive(true);
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario update(String id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setName(usuarioActualizado.getName());
            usuario.setPhone(usuarioActualizado.getPhone());
            usuario.setRole(usuarioActualizado.getRole());
            usuario.setPetsIds(usuarioActualizado.getPetsIds());
            // ✅ CORREGIDO: isActive() → getActive() (para Boolean)
            usuario.setActive(usuarioActualizado.getActive());
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    public boolean delete(String id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public Usuario login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario.get();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRole().name().replace("ROLE_", ""))
            .build();
    }
}
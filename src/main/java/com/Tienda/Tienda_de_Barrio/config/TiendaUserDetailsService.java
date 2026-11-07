package com.Tienda.Tienda_de_Barrio.config;

import java.util.List;
import java.util.Locale;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.UsuarioVO;

@Service
public class TiendaUserDetailsService implements UserDetailsService, UserDetailsPasswordService {
    private final UsuarioDAO usuarioDAO;

    public TiendaUserDetailsService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        UsuarioVO usuario = usuarioDAO.consultarPorCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe una cuenta con ese correo."));
        String rol = normalizarRol(usuario.getRol());
        List<SimpleGrantedAuthority> autoridades = rol.isBlank()
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + rol));
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .authorities(autoridades)
                .build();
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        if (!usuarioDAO.actualizarContrasenaPorCorreo(user.getUsername(), newPassword)) {
            throw new UsernameNotFoundException("No se pudo actualizar la contraseña de la cuenta.");
        }
        return User.withUserDetails(user).password(newPassword).build();
    }

    private String normalizarRol(String rol) {
        if (rol == null) {
            return "";
        }
        String normalizado = rol.trim().toUpperCase(Locale.ROOT);
        return normalizado.startsWith("ROLE_") ? normalizado.substring("ROLE_".length()) : normalizado;
    }
}

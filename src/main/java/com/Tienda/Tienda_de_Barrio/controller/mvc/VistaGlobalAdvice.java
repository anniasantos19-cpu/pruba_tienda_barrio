package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class VistaGlobalAdvice {
    @ModelAttribute("correoSesion")
    public String correoSesion(Authentication authentication) {
        return authentication == null ? "" : authentication.getName();
    }

    @ModelAttribute("esAdministrador")
    public boolean esAdministrador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(autoridad -> autoridad.equals("ROLE_ADMIN")
                        || autoridad.equals("ROLE_ADMINISTRADOR"));
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.UsuarioVO;

@Controller
public class ContrasenaMvcController {

    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public ContrasenaMvcController(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/web/contrasena")
    public String mostrarFormulario() {
        return "contrasena";
    }

    @PostMapping("/web/contrasena")
    public String cambiarContrasena(@RequestParam String contrasenaActual,
            @RequestParam String contrasenaNueva, @RequestParam String confirmarContrasena,
            Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        UsuarioVO usuario = usuarioDAO.consultarPorCorreo(authentication.getName()).orElse(null);
        if (usuario == null) {
            model.addAttribute("errorContrasena", "No se encontró tu cuenta. Vuelve a iniciar sesión.");
            return "contrasena";
        }
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            model.addAttribute("errorContrasena", "La contraseña actual no es correcta.");
            return "contrasena";
        }
        if (contrasenaNueva.length() < 8) {
            model.addAttribute("errorContrasena", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "contrasena";
        }
        if (!contrasenaNueva.equals(confirmarContrasena)) {
            model.addAttribute("errorContrasena", "La confirmación no coincide con la nueva contraseña.");
            return "contrasena";
        }
        if (passwordEncoder.matches(contrasenaNueva, usuario.getContrasena())) {
            model.addAttribute("errorContrasena", "La nueva contraseña debe ser diferente a la actual.");
            return "contrasena";
        }

        if (!usuarioDAO.actualizarContrasenaPorCorreo(usuario.getCorreo(),
                passwordEncoder.encode(contrasenaNueva))) {
            model.addAttribute("errorContrasena", "No se pudo actualizar la contraseña. Inténtalo nuevamente.");
            return "contrasena";
        }
        redirectAttributes.addFlashAttribute("exitoContrasena", "Tu contraseña se cambió correctamente.");
        return "redirect:/web/contrasena";
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.UsuarioVO;

@Controller
public class CambioContrasenaPublicoMvcController {

    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public CambioContrasenaPublicoMvcController(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/cambiar-contrasena")
    public String mostrarFormulario() {
        return "cambiar-contrasena";
    }

    @PostMapping("/cambiar-contrasena")
    public String cambiarContrasena(@RequestParam(defaultValue = "") String correo,
            @RequestParam(defaultValue = "") String contrasenaActual,
            @RequestParam(defaultValue = "") String contrasenaNueva,
            @RequestParam(defaultValue = "") String confirmarContrasena,
            Model model, RedirectAttributes redirectAttributes) {
        UsuarioVO usuario = usuarioDAO.consultarPorCorreo(correo.trim()).orElse(null);
        if (usuario == null || !passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            model.addAttribute("errorContrasena", "El correo o la contraseña actual no son correctos.");
            return "cambiar-contrasena";
        }
        if (contrasenaNueva.length() < 8) {
            model.addAttribute("errorContrasena", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "cambiar-contrasena";
        }
        if (!contrasenaNueva.equals(confirmarContrasena)) {
            model.addAttribute("errorContrasena", "La confirmación no coincide con la nueva contraseña.");
            return "cambiar-contrasena";
        }
        if (passwordEncoder.matches(contrasenaNueva, usuario.getContrasena())) {
            model.addAttribute("errorContrasena", "La nueva contraseña debe ser diferente a la actual.");
            return "cambiar-contrasena";
        }
        if (!usuarioDAO.actualizarContrasenaPorCorreo(usuario.getCorreo(),
                passwordEncoder.encode(contrasenaNueva))) {
            model.addAttribute("errorContrasena", "No se pudo actualizar la contraseña. Inténtalo nuevamente.");
            return "cambiar-contrasena";
        }
        redirectAttributes.addFlashAttribute("exitoContrasena", "Tu contraseña se cambió correctamente.");
        return "redirect:/cambiar-contrasena";
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.UsuarioVO;

@Controller
@RequestMapping("/web/usuarios")
public class UsuarioMvcController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioMvcController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioDAO.listar());
        model.addAttribute("nuevoUsuario", new UsuarioVO());
        model.addAttribute("usuarioEditar", null);
        return "usuarios";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEdicion(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        UsuarioVO usuario = usuarioDAO.consultarPorId(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se encontró la cuenta seleccionada.");
            return "redirect:/web/usuarios";
        }
        model.addAttribute("usuarios", usuarioDAO.listar());
        model.addAttribute("nuevoUsuario", new UsuarioVO());
        model.addAttribute("usuarioEditar", usuario);
        return "usuarios";
    }

    @PostMapping
    public String crear(@ModelAttribute("nuevoUsuario") UsuarioVO usuario, RedirectAttributes redirectAttributes) {
        if (estaVacio(usuario.getNombre()) || estaVacio(usuario.getCorreo())
                || estaVacio(usuario.getContrasena()) || estaVacio(usuario.getRol())) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Completa todos los campos para crear la cuenta.");
            return "redirect:/web/usuarios";
        }
        String correo = usuario.getCorreo().trim();
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Ingresa un correo electrónico válido.");
            return "redirect:/web/usuarios";
        }
        if (usuario.getContrasena().length() < 8) {
            redirectAttributes.addFlashAttribute("errorUsuario", "La contraseña debe tener al menos 8 caracteres.");
            return "redirect:/web/usuarios";
        }
        if (!esRolPermitido(usuario.getRol())) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Selecciona un rol válido.");
            return "redirect:/web/usuarios";
        }
        if (usuarioDAO.consultarPorCorreo(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Ya existe una cuenta con ese correo.");
            return "redirect:/web/usuarios";
        }
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(correo);
        usuario.setRol(usuario.getRol().trim().toUpperCase());
        usuarioDAO.crear(usuario);
        redirectAttributes.addFlashAttribute("exitoUsuario", "La cuenta se creó correctamente.");
        return "redirect:/web/usuarios";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @ModelAttribute("usuarioEditar") UsuarioVO cambios,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        if (estaVacio(cambios.getNombre()) || estaVacio(cambios.getCorreo()) || estaVacio(cambios.getRol())) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Completa nombre, correo y rol.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }
        String correo = cambios.getCorreo().trim();
        if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Ingresa un correo electrónico válido.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }
        if (!esRolPermitido(cambios.getRol())) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Selecciona un rol válido.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }
        UsuarioVO existente = usuarioDAO.consultarPorId(id).orElse(null);
        if (existente == null) {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se encontró la cuenta seleccionada.");
            return "redirect:/web/usuarios";
        }
        boolean esCuentaActiva = existente.getCorreo().equalsIgnoreCase(authentication.getName());
        if (esCuentaActiva && !existente.getCorreo().equalsIgnoreCase(correo)) {
            redirectAttributes.addFlashAttribute("errorUsuario",
                    "Para cambiar el correo de tu propia cuenta, contacta con otro administrador.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }
        if (usuarioDAO.consultarPorCorreo(correo)
                .filter(otroUsuario -> !otroUsuario.getIdUsuario().equals(id)).isPresent()) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Ya existe una cuenta con ese correo.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }
        if (esCuentaActiva && !esRolAdministrador(cambios.getRol())) {
            redirectAttributes.addFlashAttribute("errorUsuario",
                    "No puedes quitarte el rol de administrador mientras tienes la sesión iniciada.");
            return "redirect:/web/usuarios/" + id + "/editar";
        }

        existente.setNombre(cambios.getNombre().trim());
        existente.setCorreo(correo);
        existente.setRol(cambios.getRol().trim().toUpperCase());
        if (usuarioDAO.actualizarDatosBasicos(existente)) {
            redirectAttributes.addFlashAttribute("exitoUsuario", "La cuenta se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se pudo actualizar la cuenta.");
        }
        return "redirect:/web/usuarios";
    }

    @PostMapping("/{id}/rol")
    public String actualizarRol(@PathVariable int id, String rol, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (!esRolPermitido(rol)) {
            redirectAttributes.addFlashAttribute("errorUsuario", "Selecciona un rol válido.");
            return "redirect:/web/usuarios";
        }
        UsuarioVO usuario = usuarioDAO.consultarPorId(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se encontró la cuenta seleccionada.");
            return "redirect:/web/usuarios";
        }
        if (usuario.getCorreo().equalsIgnoreCase(authentication.getName()) && !esRolAdministrador(rol)) {
            redirectAttributes.addFlashAttribute("errorUsuario",
                    "No puedes quitarte el rol de administrador mientras tienes la sesión iniciada.");
            return "redirect:/web/usuarios";
        }
        usuarioDAO.actualizarRol(id, rol.trim());
        redirectAttributes.addFlashAttribute("exitoUsuario", "El rol se actualizó correctamente.");
        return "redirect:/web/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        UsuarioVO usuario = usuarioDAO.consultarPorId(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se encontró la cuenta seleccionada.");
        } else if (usuario.getCorreo().equalsIgnoreCase(authentication.getName())) {
            redirectAttributes.addFlashAttribute("errorUsuario", "No puedes eliminar tu propia cuenta.");
        } else if (usuarioDAO.eliminar(id)) {
            redirectAttributes.addFlashAttribute("exitoUsuario", "La cuenta se eliminó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorUsuario", "No se pudo eliminar la cuenta.");
        }
        return "redirect:/web/usuarios";
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean esRolAdministrador(String rol) {
        return "ADMIN".equalsIgnoreCase(rol.trim()) || "ADMINISTRADOR".equalsIgnoreCase(rol.trim());
    }

    private boolean esRolPermitido(String rol) {
        return "EMPLEADO".equalsIgnoreCase(rol == null ? "" : rol.trim())
                || "ADMINISTRADOR".equalsIgnoreCase(rol == null ? "" : rol.trim());
    }
}

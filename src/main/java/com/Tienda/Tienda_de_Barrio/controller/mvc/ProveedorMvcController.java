package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProveedorVO;

@Controller
@RequestMapping("/web/proveedores")
public class ProveedorMvcController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorMvcController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorDAO.listar());
        model.addAttribute("nuevoProveedor", new ProveedorVO());
        return "proveedores";
    }

    @PostMapping
    public String crear(@ModelAttribute("nuevoProveedor") ProveedorVO proveedor, RedirectAttributes redirectAttributes) {
        if (estaVacio(proveedor.getNombre()) || estaVacio(proveedor.getTelefono()) || estaVacio(proveedor.getDireccion())) {
            redirectAttributes.addFlashAttribute("errorProveedor", "Completa todos los campos del proveedor.");
            return "redirect:/web/proveedores";
        }

        proveedor.setNombre(proveedor.getNombre().trim());
        proveedor.setTelefono(proveedor.getTelefono().trim());
        proveedor.setDireccion(proveedor.getDireccion().trim());

        if (proveedor.getTelefono().length() < 8) {
            redirectAttributes.addFlashAttribute("errorProveedor", "El teléfono debe tener al menos 8 caracteres.");
            return "redirect:/web/proveedores";
        }

        proveedorDAO.crear(proveedor);
        redirectAttributes.addFlashAttribute("exitoProveedor", "El proveedor se registró correctamente.");
        return "redirect:/web/proveedores";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @ModelAttribute ProveedorVO proveedor,
            RedirectAttributes redirectAttributes) {
        if (estaVacio(proveedor.getNombre()) || estaVacio(proveedor.getTelefono())
                || estaVacio(proveedor.getDireccion()) || proveedor.getTelefono().trim().length() < 8) {
            redirectAttributes.addFlashAttribute("errorProveedor", "Completa los campos con valores válidos.");
            return "redirect:/web/proveedores";
        }
        proveedor.setIdProveedor(id);
        proveedor.setNombre(proveedor.getNombre().trim());
        proveedor.setTelefono(proveedor.getTelefono().trim());
        proveedor.setDireccion(proveedor.getDireccion().trim());
        if (proveedorDAO.actualizar(proveedor)) {
            redirectAttributes.addFlashAttribute("exitoProveedor", "El proveedor se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorProveedor", "No se encontró el proveedor seleccionado.");
        }
        return "redirect:/web/proveedores";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            if (proveedorDAO.eliminar(id)) {
                redirectAttributes.addFlashAttribute("exitoProveedor", "El proveedor se eliminó correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorProveedor", "No se encontró el proveedor seleccionado.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("errorProveedor",
                    "No se puede eliminar el proveedor porque tiene compras registradas.");
        }
        return "redirect:/web/proveedores";
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}

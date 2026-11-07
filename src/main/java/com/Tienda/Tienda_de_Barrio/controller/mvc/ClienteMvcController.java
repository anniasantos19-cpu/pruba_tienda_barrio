package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ClienteVO;

@Controller
@RequestMapping("/web/clientes")
public class ClienteMvcController {

    private final ClienteDAO clienteDAO;

    public ClienteMvcController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteDAO.listar());
        model.addAttribute("nuevoCliente", new ClienteVO());
        return "clientes";
    }

    @PostMapping
    public String crear(@ModelAttribute("nuevoCliente") ClienteVO cliente,
            RedirectAttributes redirectAttributes) {
        if (!esClienteValido(cliente)) {
            redirectAttributes.addFlashAttribute("errorCliente", "Completa los campos con valores válidos.");
            return "redirect:/web/clientes";
        }
        normalizar(cliente);
        clienteDAO.crear(cliente);
        redirectAttributes.addFlashAttribute("exitoCliente", "El cliente se agregó correctamente.");
        return "redirect:/web/clientes";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @ModelAttribute ClienteVO cliente,
            RedirectAttributes redirectAttributes) {
        if (!esClienteValido(cliente)) {
            redirectAttributes.addFlashAttribute("errorCliente", "Completa los campos con valores válidos.");
            return "redirect:/web/clientes";
        }
        cliente.setIdCliente(id);
        normalizar(cliente);
        if (clienteDAO.actualizar(cliente)) {
            redirectAttributes.addFlashAttribute("exitoCliente", "El cliente se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorCliente", "No se encontró el cliente seleccionado.");
        }
        return "redirect:/web/clientes";
    }

    private boolean esClienteValido(ClienteVO cliente) {
        return !estaVacio(cliente.getNombre()) && !estaVacio(cliente.getTelefono())
                && cliente.getTelefono().trim().length() >= 8 && !estaVacio(cliente.getDireccion());
    }

    private void normalizar(ClienteVO cliente) {
        cliente.setNombre(cliente.getNombre().trim());
        cliente.setTelefono(cliente.getTelefono().trim());
        cliente.setDireccion(cliente.getDireccion().trim());
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.VentaDAO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@Controller
@RequestMapping("/web/ventas")
public class VentaMvcController {

    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final RegistroOperacionService registroOperacionService;

    public VentaMvcController(VentaDAO ventaDAO, ClienteDAO clienteDAO, ProductoDAO productoDAO,
            RegistroOperacionService registroOperacionService) {
        this.ventaDAO = ventaDAO;
        this.clienteDAO = clienteDAO;
        this.productoDAO = productoDAO;
        this.registroOperacionService = registroOperacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaDAO.listar());
        model.addAttribute("fiadosPendientes", ventaDAO.listarFiadosPendientes());
        model.addAttribute("totalFiadoPendiente", ventaDAO.totalFiadoPendiente());
        model.addAttribute("clientes", clienteDAO.listar());
        model.addAttribute("productos", productoDAO.listar());
        return "ventas";
    }

    @PostMapping
    public String crear(@RequestParam Integer idCliente, @RequestParam List<Integer> idsProducto,
            @RequestParam List<Integer> cantidades, @RequestParam(defaultValue = "false") boolean fiado,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            registroOperacionService.crearVenta(authentication.getName(), idCliente, idsProducto, cantidades, fiado);
            redirectAttributes.addFlashAttribute("exitoVenta", "La venta se registró correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorVenta", exception.getMessage());
        }
        return "redirect:/web/ventas";
    }

    @PostMapping("/{id}/pagar")
    public String marcarPagado(@PathVariable int id, RedirectAttributes redirectAttributes) {
        if (ventaDAO.marcarPagado(id)) {
            redirectAttributes.addFlashAttribute("exitoVenta", "La venta fiada se marcó como pagada.");
        } else {
            redirectAttributes.addFlashAttribute("errorVenta", "La venta ya está pagada o no está fiada.");
        }
        return "redirect:/web/ventas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            if (registroOperacionService.eliminarVenta(id)) {
                redirectAttributes.addFlashAttribute("exitoVenta",
                        "La venta se eliminó y sus productos se devolvieron al inventario.");
            } else {
                redirectAttributes.addFlashAttribute("errorVenta", "No se encontró la venta seleccionada.");
            }
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorVenta", exception.getMessage());
        }
        return "redirect:/web/ventas";
    }
}

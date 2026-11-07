package com.Tienda.Tienda_de_Barrio.controller.mvc;

import java.math.BigDecimal;
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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.CompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@Controller
@RequestMapping("/web/compras")
public class CompraMvcController {

    private final CompraDAO compraDAO;
    private final ProveedorDAO proveedorDAO;
    private final ProductoDAO productoDAO;
    private final RegistroOperacionService registroOperacionService;

    public CompraMvcController(CompraDAO compraDAO, ProveedorDAO proveedorDAO, ProductoDAO productoDAO,
            RegistroOperacionService registroOperacionService) {
        this.compraDAO = compraDAO;
        this.proveedorDAO = proveedorDAO;
        this.productoDAO = productoDAO;
        this.registroOperacionService = registroOperacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("compras", compraDAO.listar());
        model.addAttribute("proveedores", proveedorDAO.listar());
        model.addAttribute("productos", productoDAO.listar());
        return "compras";
    }

    @PostMapping
    public String crear(@RequestParam Integer idProveedor, @RequestParam List<Integer> idsProducto,
            @RequestParam List<Integer> cantidades, @RequestParam List<BigDecimal> precios,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            registroOperacionService.crearCompra(authentication.getName(), idProveedor, idsProducto, cantidades,
                    precios);
            redirectAttributes.addFlashAttribute("exitoCompra", "La compra se registró correctamente.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorCompra", exception.getMessage());
        }
        return "redirect:/web/compras";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            if (registroOperacionService.eliminarCompra(id)) {
                redirectAttributes.addFlashAttribute("exitoCompra",
                        "La compra se eliminó y sus productos se descontaron del inventario.");
            } else {
                redirectAttributes.addFlashAttribute("errorCompra", "No se encontró la compra seleccionada.");
            }
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorCompra", exception.getMessage());
        }
        return "redirect:/web/compras";
    }
}

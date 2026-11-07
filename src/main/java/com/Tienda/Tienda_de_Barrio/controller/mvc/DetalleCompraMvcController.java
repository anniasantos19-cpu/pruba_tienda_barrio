package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleCompraDAO;

@Controller
@RequestMapping("/web/compras/{idCompra}/detalles")
public class DetalleCompraMvcController {
    private final DetalleCompraDAO detalleCompraDAO;

    public DetalleCompraMvcController(DetalleCompraDAO detalleCompraDAO) {
        this.detalleCompraDAO = detalleCompraDAO;
    }

    @GetMapping
    public String listar(@PathVariable int idCompra, Model model) {
        model.addAttribute("idCompra", idCompra);
        model.addAttribute("detalles", detalleCompraDAO.listarPorCompra(idCompra));
        return "detalles-compra";
    }
}

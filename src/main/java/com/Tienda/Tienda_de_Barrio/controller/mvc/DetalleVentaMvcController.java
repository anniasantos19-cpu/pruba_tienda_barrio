package com.Tienda.Tienda_de_Barrio.controller.mvc;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleVentaDAO;

@Controller
@RequestMapping("/web/ventas/{idVenta}/detalles")
public class DetalleVentaMvcController {
    private final DetalleVentaDAO detalleVentaDAO;

    public DetalleVentaMvcController(DetalleVentaDAO detalleVentaDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
    }

    @GetMapping
    public String listar(@PathVariable int idVenta, Model model) {
        model.addAttribute("idVenta", idVenta);
        model.addAttribute("detalles", detalleVentaDAO.listarPorVenta(idVenta));
        return "detalles-venta";
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.ProductoVO;

@Controller
@RequestMapping("/web/productos")
public class ProductoMvcController {

    private static final List<String> CATEGORIAS_BASE = List.of(
            "Abarrotes", "Bebidas", "Carnes y embutidos", "Cuidado personal", "Frutas y verduras",
            "Lácteos y huevos", "Limpieza", "Otros", "Panadería", "Snacks y dulces");

    private final ProductoDAO productoDAO;

    public ProductoMvcController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public String listar(Model model) {
        List<ProductoVO> productos = productoDAO.listar();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriasDisponibles(productos));
        model.addAttribute("nuevoProducto", new ProductoVO());
        return "productos";
    }

    @PostMapping
    public String crear(@ModelAttribute("nuevoProducto") ProductoVO producto,
            RedirectAttributes redirectAttributes) {
        if (!esProductoValido(producto)) {
            redirectAttributes.addFlashAttribute("errorProducto", "Completa los campos con valores válidos.");
            return "redirect:/web/productos";
        }
        normalizar(producto);
        productoDAO.crear(producto);
        redirectAttributes.addFlashAttribute("exitoProducto", "El producto se agregó correctamente.");
        return "redirect:/web/productos";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable int id, @ModelAttribute ProductoVO producto,
            RedirectAttributes redirectAttributes) {
        if (!esProductoValido(producto)) {
            redirectAttributes.addFlashAttribute("errorProducto", "Completa los campos con valores válidos.");
            return "redirect:/web/productos";
        }
        producto.setIdProducto(id);
        normalizar(producto);
        if (productoDAO.actualizar(producto)) {
            redirectAttributes.addFlashAttribute("exitoProducto", "El producto se actualizó correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorProducto", "No se encontró el producto seleccionado.");
        }
        return "redirect:/web/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            if (productoDAO.eliminar(id)) {
                redirectAttributes.addFlashAttribute("exitoProducto", "El producto se eliminó correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorProducto", "No se encontró el producto seleccionado.");
            }
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("errorProducto",
                    "No se puede eliminar el producto porque aparece en compras o ventas registradas.");
        }
        return "redirect:/web/productos";
    }

    private boolean esProductoValido(ProductoVO producto) {
        return !estaVacio(producto.getNombre()) && !estaVacio(producto.getCategoria())
                && !estaVacio(producto.getMarca()) && producto.getPrecio() != null
                && producto.getPrecio().signum() > 0 && producto.getCantidad() != null
                && producto.getCantidad() >= 0;
    }

    private void normalizar(ProductoVO producto) {
        producto.setNombre(producto.getNombre().trim());
        producto.setCategoria(producto.getCategoria().trim());
        producto.setMarca(producto.getMarca().trim());
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private List<String> categoriasDisponibles(List<ProductoVO> productos) {
        List<String> categorias = new ArrayList<>(CATEGORIAS_BASE);
        for (ProductoVO producto : productos) {
            String categoria = producto.getCategoria();
            if (!estaVacio(categoria) && categorias.stream().noneMatch(categoriaExistente
                    -> categoriaExistente.equalsIgnoreCase(categoria.trim()))) {
                categorias.add(categoria.trim());
            }
        }
        categorias.sort(String.CASE_INSENSITIVE_ORDER);
        return categorias;
    }
}

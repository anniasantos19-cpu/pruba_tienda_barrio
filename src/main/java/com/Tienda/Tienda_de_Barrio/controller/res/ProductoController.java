package com.Tienda.Tienda_de_Barrio.controller.res;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProductoVO;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoDAO productoDAO;

    public ProductoController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @PostMapping
    public ResponseEntity<ProductoVO> crear(@RequestBody ProductoVO producto) {
        int id = productoDAO.crear(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + id)).body(producto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoVO> consultarPorId(@PathVariable int id) {
        return productoDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductoVO> listar() {
        return productoDAO.listar();
    }

    @GetMapping("/categoria/{categoria}")
    public List<ProductoVO> buscarPorCategoria(@PathVariable String categoria) {
        return productoDAO.buscarPorCategoria(categoria);
    }

    @GetMapping("/buscar")
    public List<ProductoVO> buscarPorNombre(@RequestParam String nombre) {
        return productoDAO.buscarPorNombre(nombre);
    }

    @GetMapping("/pocas-unidades")
    public List<ProductoVO> buscarConPocasUnidades(@RequestParam(defaultValue = "5") int maximo) {
        return productoDAO.buscarConPocasUnidades(maximo);
    }

    @GetMapping("/ordenados-precio")
    public List<ProductoVO> listarOrdenadosPorPrecio() {
        return productoDAO.listarOrdenadosPorPrecio();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody ProductoVO producto) {
        producto.setIdProducto(id);
        return productoDAO.actualizar(producto)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return productoDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

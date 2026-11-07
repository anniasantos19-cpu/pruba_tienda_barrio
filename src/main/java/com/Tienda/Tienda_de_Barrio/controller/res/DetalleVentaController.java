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
import org.springframework.web.bind.annotation.RestController;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleVentaDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.DetalleVentaVO;

@RestController
@RequestMapping("/api/detalles-ventas")
public class DetalleVentaController {
    private final DetalleVentaDAO detalleVentaDAO;

    public DetalleVentaController(DetalleVentaDAO detalleVentaDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
    }

    @PostMapping
    public ResponseEntity<DetalleVentaVO> crear(@RequestBody DetalleVentaVO detalle) {
        int id = detalleVentaDAO.crear(detalle);
        return ResponseEntity.created(URI.create("/api/detalles-ventas/" + id)).body(detalle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaVO> consultarPorId(@PathVariable int id) {
        return detalleVentaDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DetalleVentaVO> listar() {
        return detalleVentaDAO.listar();
    }

    @GetMapping("/venta/{idVenta}")
    public List<DetalleVentaVO> listarPorVenta(@PathVariable int idVenta) {
        return detalleVentaDAO.listarPorVenta(idVenta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody DetalleVentaVO detalle) {
        detalle.setIdDetalle(id);
        return detalleVentaDAO.actualizar(detalle)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return detalleVentaDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

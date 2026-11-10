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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleCompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.DetalleCompraVO;

@RestController
@RequestMapping("/api/detalles-compras")
public class DetalleCompraController {
    private final DetalleCompraDAO detalleCompraDAO;

    public DetalleCompraController(DetalleCompraDAO detalleCompraDAO) {
        this.detalleCompraDAO = detalleCompraDAO;
    }

    @PostMapping
    public ResponseEntity<DetalleCompraVO> crear(@RequestBody DetalleCompraVO detalle) {
        int id = detalleCompraDAO.crear(detalle);
        return ResponseEntity.created(URI.create("/api/detalles-compras/" + id)).body(detalle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCompraVO> consultarPorId(@PathVariable int id) {
        return detalleCompraDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<DetalleCompraVO> listar() {
        return detalleCompraDAO.listar();
    }

    @GetMapping("/compra/{idCompra}")
    public List<DetalleCompraVO> listarPorCompra(@PathVariable int idCompra) {
        return detalleCompraDAO.listarPorCompra(idCompra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody DetalleCompraVO detalle) {
        detalle.setIdDetalle(id);
        return detalleCompraDAO.actualizar(detalle)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return detalleCompraDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

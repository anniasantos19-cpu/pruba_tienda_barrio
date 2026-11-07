package com.Tienda.Tienda_de_Barrio.controller.res;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.VentaDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.VentaVO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    private final VentaDAO ventaDAO;
    private final RegistroOperacionService registroOperacionService;

    public VentaController(VentaDAO ventaDAO, RegistroOperacionService registroOperacionService) {
        this.ventaDAO = ventaDAO;
        this.registroOperacionService = registroOperacionService;
    }

    @PostMapping
    public ResponseEntity<VentaVO> crear(@RequestBody VentaVO venta) {
        int id = ventaDAO.crear(venta);
        return ResponseEntity.created(URI.create("/api/ventas/" + id)).body(venta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaVO> consultarPorId(@PathVariable int id) {
        return ventaDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<VentaVO> listar() {
        return ventaDAO.listar();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody VentaVO venta) {
        venta.setIdVenta(id);
        return ventaDAO.actualizar(venta)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            return registroOperacionService.eliminarVenta(id)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

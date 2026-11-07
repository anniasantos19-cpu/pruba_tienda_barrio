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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.CompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.CompraVO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {
    private final CompraDAO compraDAO;
    private final RegistroOperacionService registroOperacionService;

    public CompraController(CompraDAO compraDAO, RegistroOperacionService registroOperacionService) {
        this.compraDAO = compraDAO;
        this.registroOperacionService = registroOperacionService;
    }

    @PostMapping
    public ResponseEntity<CompraVO> crear(@RequestBody CompraVO compra) {
        int id = compraDAO.crear(compra);
        return ResponseEntity.created(URI.create("/api/compras/" + id)).body(compra);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraVO> consultarPorId(@PathVariable int id) {
        return compraDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<CompraVO> listar() {
        return compraDAO.listar();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody CompraVO compra) {
        compra.setIdCompra(id);
        return compraDAO.actualizar(compra)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            return registroOperacionService.eliminarCompra(id)
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

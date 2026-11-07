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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProveedorVO;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorDAO proveedorDAO;

    public ProveedorController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @PostMapping
    public ResponseEntity<ProveedorVO> crear(@RequestBody ProveedorVO proveedor) {
        int id = proveedorDAO.crear(proveedor);
        return ResponseEntity.created(URI.create("/api/proveedores/" + id)).body(proveedor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorVO> consultarPorId(@PathVariable int id) {
        return proveedorDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProveedorVO> listar() {
        return proveedorDAO.listar();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody ProveedorVO proveedor) {
        proveedor.setIdProveedor(id);
        return proveedorDAO.actualizar(proveedor)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return proveedorDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.ClienteVO;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    @PostMapping
    public ResponseEntity<ClienteVO> crear(@RequestBody ClienteVO cliente) {
        int id = clienteDAO.crear(cliente);
        return ResponseEntity.created(URI.create("/api/clientes/" + id)).body(cliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteVO> consultarPorId(@PathVariable int id) {
        return clienteDAO.consultarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClienteVO> listar() {
        return clienteDAO.listar();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody ClienteVO cliente) {
        cliente.setIdCliente(id);
        return clienteDAO.actualizar(cliente)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return clienteDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

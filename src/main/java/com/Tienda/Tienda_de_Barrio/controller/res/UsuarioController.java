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

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.UsuarioVO;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioDAO usuarioDAO;

    public UsuarioController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @PostMapping
    public ResponseEntity<UsuarioRespuesta> crear(@RequestBody UsuarioVO usuario) {
        int id = usuarioDAO.crear(usuario);
        return ResponseEntity.created(URI.create("/api/usuarios/" + id))
                .body(UsuarioRespuesta.desde(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuesta> consultarPorId(@PathVariable int id) {
        return usuarioDAO.consultarPorId(id)
                .map(UsuarioRespuesta::desde)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UsuarioRespuesta> listar() {
        return usuarioDAO.listar().stream().map(UsuarioRespuesta::desde).toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable int id, @RequestBody UsuarioVO usuario) {
        usuario.setIdUsuario(id);
        return usuarioDAO.actualizar(usuario)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return usuarioDAO.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private record UsuarioRespuesta(Integer idUsuario, String nombre, String correo, String rol) {
        private static UsuarioRespuesta desde(UsuarioVO usuario) {
            return new UsuarioRespuesta(usuario.getIdUsuario(), usuario.getNombre(), usuario.getCorreo(),
                    usuario.getRol());
        }
    }
}

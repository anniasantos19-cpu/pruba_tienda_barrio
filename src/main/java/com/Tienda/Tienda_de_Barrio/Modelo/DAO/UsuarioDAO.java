package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.modelo.VO.UsuarioVO;

@Repository
public class UsuarioDAO {

    private static final String COLUMNAS = "id_usuario, nombre, correo, contrasena, rol";
    private static final RowMapper<UsuarioVO> MAPPER = UsuarioDAO::mapear;

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDAO(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public int crear(UsuarioVO usuario) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Usuarios (nombre, correo, contrasena, rol) VALUES (?, ?, ?, ?)",
                usuario.getNombre(), usuario.getCorreo(), protegerContrasena(usuario.getContrasena()), usuario.getRol());
        usuario.setIdUsuario(id);
        return id;
    }

    public Optional<UsuarioVO> consultarPorId(int idUsuario) {
        return consultarUno("SELECT " + COLUMNAS + " FROM Usuarios WHERE id_usuario = ?", idUsuario);
    }

    public Optional<UsuarioVO> consultarPorCorreo(String correo) {
        return consultarUno("SELECT " + COLUMNAS + " FROM Usuarios WHERE correo = ?", correo);
    }

    public List<UsuarioVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Usuarios ORDER BY id_usuario", MAPPER);
    }

    public boolean actualizar(UsuarioVO usuario) {
        return jdbcTemplate.update(
                "UPDATE Usuarios SET nombre = ?, correo = ?, contrasena = ?, rol = ? WHERE id_usuario = ?",
                usuario.getNombre(), usuario.getCorreo(), protegerContrasena(usuario.getContrasena()), usuario.getRol(),
                usuario.getIdUsuario()) > 0;
    }

    public boolean actualizarRol(int idUsuario, String rol) {
        return jdbcTemplate.update("UPDATE Usuarios SET rol = ? WHERE id_usuario = ?", rol, idUsuario) > 0;
    }

    public boolean actualizarDatosBasicos(UsuarioVO usuario) {
        return jdbcTemplate.update("UPDATE Usuarios SET nombre = ?, correo = ?, rol = ? WHERE id_usuario = ?",
                usuario.getNombre(), usuario.getCorreo(), usuario.getRol(), usuario.getIdUsuario()) > 0;
    }

    public boolean actualizarContrasenaPorCorreo(String correo, String contrasena) {
        return jdbcTemplate.update("UPDATE Usuarios SET contrasena = ? WHERE correo = ?", contrasena, correo) > 0;
    }

    public boolean eliminar(int idUsuario) {
        return jdbcTemplate.update("DELETE FROM Usuarios WHERE id_usuario = ?", idUsuario) > 0;
    }

    private Optional<UsuarioVO> consultarUno(String sql, Object parametro) {
        return jdbcTemplate.query(sql, MAPPER, parametro).stream().findFirst();
    }

    private String protegerContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()
                || contrasena.startsWith("$2a$") || contrasena.startsWith("$2b$") || contrasena.startsWith("$2y$")) {
            return contrasena;
        }
        return passwordEncoder.encode(contrasena);
    }

    private static UsuarioVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new UsuarioVO(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("correo"),
                rs.getString("contrasena"), rs.getString("rol"));
    }
}

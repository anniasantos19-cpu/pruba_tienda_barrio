package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.modelo.VO.ProveedorVO;

@Repository
public class ProveedorDAO {
    private static final String COLUMNAS = "id_proveedor, nombre, telefono, direccion";
    private static final RowMapper<ProveedorVO> MAPPER = ProveedorDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public ProveedorDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(ProveedorVO proveedor) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Proveedores (nombre, telefono, direccion) VALUES (?, ?, ?)",
                proveedor.getNombre(), proveedor.getTelefono(), proveedor.getDireccion());
        proveedor.setIdProveedor(id);
        return id;
    }

    public Optional<ProveedorVO> consultarPorId(int idProveedor) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Proveedores WHERE id_proveedor = ?", MAPPER,
                idProveedor).stream().findFirst();
    }

    public List<ProveedorVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Proveedores ORDER BY nombre", MAPPER);
    }

    public boolean actualizar(ProveedorVO proveedor) {
        return jdbcTemplate.update(
                "UPDATE Proveedores SET nombre = ?, telefono = ?, direccion = ? WHERE id_proveedor = ?",
                proveedor.getNombre(), proveedor.getTelefono(), proveedor.getDireccion(),
                proveedor.getIdProveedor()) > 0;
    }

    public boolean eliminar(int idProveedor) {
        return jdbcTemplate.update("DELETE FROM Proveedores WHERE id_proveedor = ?", idProveedor) > 0;
    }

    private static ProveedorVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new ProveedorVO(rs.getInt("id_proveedor"), rs.getString("nombre"), rs.getString("telefono"),
                rs.getString("direccion"));
    }
}

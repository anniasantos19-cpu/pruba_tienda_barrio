package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.Modelo.VO.CompraVO;

@Repository
public class CompraDAO {
    private static final String COLUMNAS = "id_compra, id_usuario, id_proveedor, fecha, total";
    private static final RowMapper<CompraVO> MAPPER = CompraDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public CompraDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(CompraVO compra) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Compras (id_usuario, id_proveedor, fecha, total) VALUES (?, ?, ?, ?)",
                compra.getIdUsuario(), compra.getIdProveedor(), compra.getFecha(), compra.getTotal());
        compra.setIdCompra(id);
        return id;
    }

    public Optional<CompraVO> consultarPorId(int idCompra) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Compras WHERE id_compra = ?", MAPPER, idCompra)
                .stream().findFirst();
    }

    public List<CompraVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Compras ORDER BY fecha DESC, id_compra DESC",
                MAPPER);
    }

    public boolean actualizar(CompraVO compra) {
        return jdbcTemplate.update(
                "UPDATE Compras SET id_usuario = ?, id_proveedor = ?, fecha = ?, total = ? WHERE id_compra = ?",
                compra.getIdUsuario(), compra.getIdProveedor(), compra.getFecha(), compra.getTotal(),
                compra.getIdCompra()) > 0;
    }

    public boolean eliminar(int idCompra) {
        return jdbcTemplate.update("DELETE FROM Compras WHERE id_compra = ?", idCompra) > 0;
    }

    private static CompraVO mapear(ResultSet rs, int rowNum) throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha");
        return new CompraVO(rs.getInt("id_compra"), rs.getObject("id_usuario", Integer.class),
                rs.getObject("id_proveedor", Integer.class),
                fecha == null ? null : fecha.toLocalDateTime(), rs.getBigDecimal("total"));
    }
}

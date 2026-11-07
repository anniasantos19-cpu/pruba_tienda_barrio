package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.Modelo.VO.DetalleCompraVO;

@Repository
public class DetalleCompraDAO {
    private static final String COLUMNAS = "id_detalle, id_compra, id_producto, cantidad, precio";
    private static final RowMapper<DetalleCompraVO> MAPPER = DetalleCompraDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public DetalleCompraDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(DetalleCompraVO detalle) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Detalle_Compras (id_compra, id_producto, cantidad, precio) VALUES (?, ?, ?, ?)",
                detalle.getIdCompra(), detalle.getIdProducto(), detalle.getCantidad(), detalle.getPrecio());
        detalle.setIdDetalle(id);
        return id;
    }

    public Optional<DetalleCompraVO> consultarPorId(int idDetalle) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Compras WHERE id_detalle = ?", MAPPER,
                idDetalle).stream().findFirst();
    }

    public List<DetalleCompraVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Compras ORDER BY id_compra, id_detalle",
                MAPPER);
    }

    public List<DetalleCompraVO> listarPorCompra(int idCompra) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Compras WHERE id_compra = ? "
                + "ORDER BY id_detalle", MAPPER, idCompra);
    }

    public boolean actualizar(DetalleCompraVO detalle) {
        return jdbcTemplate.update(
                "UPDATE Detalle_Compras SET id_compra = ?, id_producto = ?, cantidad = ?, precio = ? "
                        + "WHERE id_detalle = ?",
                detalle.getIdCompra(), detalle.getIdProducto(), detalle.getCantidad(), detalle.getPrecio(),
                detalle.getIdDetalle()) > 0;
    }

    public boolean eliminar(int idDetalle) {
        return jdbcTemplate.update("DELETE FROM Detalle_Compras WHERE id_detalle = ?", idDetalle) > 0;
    }

    public int eliminarPorCompra(int idCompra) {
        return jdbcTemplate.update("DELETE FROM Detalle_Compras WHERE id_compra = ?", idCompra);
    }

    private static DetalleCompraVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new DetalleCompraVO(rs.getInt("id_detalle"), rs.getObject("id_compra", Integer.class),
                rs.getObject("id_producto", Integer.class), rs.getObject("cantidad", Integer.class),
                rs.getBigDecimal("precio"));
    }
}

package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.modelo.VO.DetalleVentaVO;

@Repository
public class DetalleVentaDAO {
    private static final String COLUMNAS = "id_detalle, id_venta, id_producto, cantidad, precio";
    private static final RowMapper<DetalleVentaVO> MAPPER = DetalleVentaDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public DetalleVentaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(DetalleVentaVO detalle) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Detalle_Ventas (id_venta, id_producto, cantidad, precio) VALUES (?, ?, ?, ?)",
                detalle.getIdVenta(), detalle.getIdProducto(), detalle.getCantidad(), detalle.getPrecio());
        detalle.setIdDetalle(id);
        return id;
    }

    public Optional<DetalleVentaVO> consultarPorId(int idDetalle) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Ventas WHERE id_detalle = ?", MAPPER,
                idDetalle).stream().findFirst();
    }

    public List<DetalleVentaVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Ventas ORDER BY id_venta, id_detalle",
                MAPPER);
    }

    public List<DetalleVentaVO> listarPorVenta(int idVenta) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Detalle_Ventas WHERE id_venta = ? "
                + "ORDER BY id_detalle", MAPPER, idVenta);
    }

    public boolean actualizar(DetalleVentaVO detalle) {
        return jdbcTemplate.update(
                "UPDATE Detalle_Ventas SET id_venta = ?, id_producto = ?, cantidad = ?, precio = ? "
                        + "WHERE id_detalle = ?",
                detalle.getIdVenta(), detalle.getIdProducto(), detalle.getCantidad(), detalle.getPrecio(),
                detalle.getIdDetalle()) > 0;
    }

    public boolean eliminar(int idDetalle) {
        return jdbcTemplate.update("DELETE FROM Detalle_Ventas WHERE id_detalle = ?", idDetalle) > 0;
    }

    public int eliminarPorVenta(int idVenta) {
        return jdbcTemplate.update("DELETE FROM Detalle_Ventas WHERE id_venta = ?", idVenta);
    }

    private static DetalleVentaVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new DetalleVentaVO(rs.getInt("id_detalle"), rs.getObject("id_venta", Integer.class),
                rs.getObject("id_producto", Integer.class), rs.getObject("cantidad", Integer.class),
                rs.getBigDecimal("precio"));
    }
}

package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProductoVO;

@Repository
public class ProductoDAO {

    private static final String COLUMNAS = "id_producto, nombre, categoria, marca, precio, cantidad";
    private static final RowMapper<ProductoVO> MAPPER = ProductoDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public ProductoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(ProductoVO producto) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Productos (nombre, categoria, marca, precio, cantidad) VALUES (?, ?, ?, ?, ?)",
                producto.getNombre(), producto.getCategoria(), producto.getMarca(), producto.getPrecio(),
                producto.getCantidad());
        producto.setIdProducto(id);
        return id;
    }

    public Optional<ProductoVO> consultarPorId(int idProducto) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Productos WHERE id_producto = ?", MAPPER, idProducto)
                .stream().findFirst();
    }

    public List<ProductoVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Productos ORDER BY id_producto", MAPPER);
    }

    public long contar() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Productos", Long.class);
    }

    public List<ProductoVO> buscarPorCategoria(String categoria) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Productos WHERE categoria = ? ORDER BY nombre",
                MAPPER, categoria);
    }

    public List<ProductoVO> buscarPorNombre(String nombre) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Productos WHERE nombre = ? ORDER BY id_producto",
                MAPPER, nombre);
    }

    public List<ProductoVO> buscarConPocasUnidades(int cantidadMaxima) {
        return jdbcTemplate.query(
                "SELECT " + COLUMNAS + " FROM Productos WHERE cantidad <= ? ORDER BY cantidad, nombre",
                MAPPER, cantidadMaxima);
    }

    public List<ProductoVO> listarOrdenadosPorPrecio() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Productos ORDER BY precio ASC, nombre ASC", MAPPER);
    }

    public boolean actualizar(ProductoVO producto) {
        return jdbcTemplate.update(
                "UPDATE Productos SET nombre = ?, categoria = ?, marca = ?, precio = ?, cantidad = ? "
                + "WHERE id_producto = ?",
                producto.getNombre(), producto.getCategoria(), producto.getMarca(), producto.getPrecio(),
                producto.getCantidad(), producto.getIdProducto()) > 0;
    }

    public boolean descontarExistencias(int idProducto, int cantidad) {
        return jdbcTemplate.update("UPDATE Productos SET cantidad = cantidad - ? "
                + "WHERE id_producto = ? AND cantidad >= ?", cantidad, idProducto, cantidad) > 0;
    }

    public boolean aumentarExistencias(int idProducto, int cantidad) {
        return jdbcTemplate.update("UPDATE Productos SET cantidad = cantidad + ? WHERE id_producto = ?",
                cantidad, idProducto) > 0;
    }

    public boolean retirarExistencias(int idProducto, int cantidad) {
        return jdbcTemplate.update("UPDATE Productos SET cantidad = cantidad - ? "
                + "WHERE id_producto = ? AND cantidad >= ?", cantidad, idProducto, cantidad) > 0;
    }

    public boolean eliminar(int idProducto) {
        return jdbcTemplate.update("DELETE FROM Productos WHERE id_producto = ?", idProducto) > 0;
    }

    private static ProductoVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new ProductoVO(rs.getInt("id_producto"), rs.getString("nombre"), rs.getString("categoria"),
                rs.getString("marca"), rs.getBigDecimal("precio"), rs.getObject("cantidad", Integer.class));
    }
}

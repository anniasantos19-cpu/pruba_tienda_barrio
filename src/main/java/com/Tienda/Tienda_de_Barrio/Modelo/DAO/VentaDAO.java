package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.Modelo.VO.SaldoFiadoVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.VentaVO;

@Repository
public class VentaDAO {

    private static final String COLUMNAS = "v.id_venta, v.id_usuario, v.id_cliente, v.fecha, v.total, "
            + "v.es_fiado, v.pagado, c.nombre AS nombre_cliente";
    private static final RowMapper<VentaVO> MAPPER = VentaDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public VentaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(VentaVO venta) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Ventas (id_usuario, id_cliente, fecha, total, es_fiado, pagado) "
                + "VALUES (?, ?, ?, ?, ?, ?)",
                venta.getIdUsuario(), venta.getIdCliente(), venta.getFecha(), venta.getTotal(), venta.isFiado(),
                venta.isPagado());
        venta.setIdVenta(id);
        return id;
    }

    public Optional<VentaVO> consultarPorId(int idVenta) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Ventas v "
                + "LEFT JOIN Clientes c ON c.id_cliente = v.id_cliente WHERE v.id_venta = ?", MAPPER, idVenta)
                .stream().findFirst();
    }

    public List<VentaVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Ventas v "
                + "LEFT JOIN Clientes c ON c.id_cliente = v.id_cliente "
                + "ORDER BY v.fecha DESC, v.id_venta DESC", MAPPER);
    }

    public BigDecimal totalFiadoPendiente() {
        return jdbcTemplate.queryForObject("SELECT COALESCE(SUM(total), 0) FROM Ventas "
                + "WHERE es_fiado = TRUE AND pagado = FALSE", BigDecimal.class);
    }

    public List<SaldoFiadoVO> listarFiadosPendientes() {
        return jdbcTemplate.query("SELECT c.id_cliente, c.nombre, SUM(v.total) AS saldo_pendiente, "
                + "COUNT(v.id_venta) AS ventas_pendientes FROM Ventas v "
                + "INNER JOIN Clientes c ON c.id_cliente = v.id_cliente "
                + "WHERE v.es_fiado = TRUE AND v.pagado = FALSE "
                + "GROUP BY c.id_cliente, c.nombre ORDER BY c.nombre", (rs, rowNum)
                -> new SaldoFiadoVO(rs.getInt("id_cliente"), rs.getString("nombre"),
                        rs.getBigDecimal("saldo_pendiente"), rs.getInt("ventas_pendientes")));
    }

    public boolean marcarPagado(int idVenta) {
        return jdbcTemplate.update("UPDATE Ventas SET pagado = TRUE "
                + "WHERE id_venta = ? AND es_fiado = TRUE AND pagado = FALSE", idVenta) > 0;
    }

    public boolean actualizar(VentaVO venta) {
        return jdbcTemplate.update(
                "UPDATE Ventas SET id_usuario = ?, id_cliente = ?, fecha = ?, total = ? WHERE id_venta = ?",
                venta.getIdUsuario(), venta.getIdCliente(), venta.getFecha(), venta.getTotal(),
                venta.getIdVenta()) > 0;
    }

    public boolean eliminar(int idVenta) {
        return jdbcTemplate.update("DELETE FROM Ventas WHERE id_venta = ?", idVenta) > 0;
    }

    private static VentaVO mapear(ResultSet rs, int rowNum) throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha");
        VentaVO venta = new VentaVO(rs.getInt("id_venta"), rs.getObject("id_usuario", Integer.class),
                rs.getObject("id_cliente", Integer.class),
                fecha == null ? null : fecha.toLocalDateTime(), rs.getBigDecimal("total"));
        venta.setFiado(rs.getBoolean("es_fiado"));
        venta.setPagado(rs.getBoolean("pagado"));
        venta.setNombreCliente(rs.getString("nombre_cliente"));
        return venta;
    }
}

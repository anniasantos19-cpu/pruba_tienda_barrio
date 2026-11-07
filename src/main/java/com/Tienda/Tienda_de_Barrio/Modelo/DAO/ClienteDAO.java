package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Tienda.Tienda_de_Barrio.Modelo.VO.ClienteVO;

@Repository
public class ClienteDAO {
    private static final String COLUMNAS = "id_cliente, nombre, telefono, direccion";
    private static final RowMapper<ClienteVO> MAPPER = ClienteDAO::mapear;

    private final JdbcTemplate jdbcTemplate;

    public ClienteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int crear(ClienteVO cliente) {
        int id = JdbcDAOUtils.insertarYObtenerId(jdbcTemplate,
                "INSERT INTO Clientes (nombre, telefono, direccion) VALUES (?, ?, ?)",
                cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion());
        cliente.setIdCliente(id);
        return id;
    }

    public Optional<ClienteVO> consultarPorId(int idCliente) {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Clientes WHERE id_cliente = ?", MAPPER, idCliente)
                .stream().findFirst();
    }

    public List<ClienteVO> listar() {
        return jdbcTemplate.query("SELECT " + COLUMNAS + " FROM Clientes ORDER BY nombre", MAPPER);
    }

    public boolean actualizar(ClienteVO cliente) {
        return jdbcTemplate.update(
                "UPDATE Clientes SET nombre = ?, telefono = ?, direccion = ? WHERE id_cliente = ?",
                cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion(), cliente.getIdCliente()) > 0;
    }

    public boolean eliminar(int idCliente) {
        return jdbcTemplate.update("DELETE FROM Clientes WHERE id_cliente = ?", idCliente) > 0;
    }

    private static ClienteVO mapear(ResultSet rs, int rowNum) throws SQLException {
        return new ClienteVO(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("telefono"),
                rs.getString("direccion"));
    }
}

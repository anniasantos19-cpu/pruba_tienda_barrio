package com.Tienda.Tienda_de_Barrio.Modelo.DAO;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

final class JdbcDAOUtils {
    private JdbcDAOUtils() {
    }

    static int insertarYObtenerId(JdbcTemplate jdbcTemplate, String sql, Object... parametros) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }
            return statement;
        }, keyHolder);

        Number idGenerado = keyHolder.getKey();
        if (idGenerado == null) {
            throw new DataRetrievalFailureException("La base de datos no devolvió el identificador generado.");
        }
        return idGenerado.intValue();
    }
}

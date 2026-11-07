package com.Tienda.Tienda_de_Barrio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.UsuarioVO;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioDAO usuarioDAO;
    private final String nombre;
    private final String correo;
    private final String contrasena;

    public AdminBootstrap(UsuarioDAO usuarioDAO,
            @Value("${tienda.bootstrap-admin.nombre:}") String nombre,
            @Value("${tienda.bootstrap-admin.correo:}") String correo,
            @Value("${tienda.bootstrap-admin.contrasena:}") String contrasena) {
        this.usuarioDAO = usuarioDAO;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    @Override
    public void run(String... args) {
        boolean configurado = !nombre.isBlank() && !correo.isBlank() && !contrasena.isBlank();
        boolean parcialmenteConfigurado = !nombre.isBlank() || !correo.isBlank() || !contrasena.isBlank();
        if (!configurado) {
            if (parcialmenteConfigurado) {
                throw new IllegalStateException(
                        "Configura nombre, correo y contraseña para crear el administrador inicial.");
            }
            return;
        }
        if (contrasena.length() < 8) {
            throw new IllegalStateException("La contraseña del administrador inicial debe tener al menos 8 caracteres.");
        }
        if (usuarioDAO.consultarPorCorreo(correo.trim()).isPresent()) {
            LOGGER.info("La cuenta de configuración inicial ya existe; no se modificó.");
            return;
        }

        UsuarioVO administrador = new UsuarioVO();
        administrador.setNombre(nombre.trim());
        administrador.setCorreo(correo.trim());
        administrador.setContrasena(contrasena);
        administrador.setRol("ADMINISTRADOR");
        usuarioDAO.crear(administrador);
        LOGGER.info("Se creó la cuenta de administrador inicial configurada.");
    }
}

package com.Tienda.Tienda_de_Barrio.controller.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.CompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleCompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleVentaDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.VentaDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ClienteVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProductoVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProveedorVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.UsuarioVO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@ExtendWith(MockitoExtension.class)
class MvcControllerUnitTests {

    @Mock private ClienteDAO clienteDAO;
    @Mock private CompraDAO compraDAO;
    @Mock private DetalleCompraDAO detalleCompraDAO;
    @Mock private DetalleVentaDAO detalleVentaDAO;
    @Mock private ProductoDAO productoDAO;
    @Mock private ProveedorDAO proveedorDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private VentaDAO ventaDAO;
    @Mock private RegistroOperacionService registroOperacionService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Authentication authentication;

    @InjectMocks private CambioContrasenaPublicoMvcController cambioContrasenaPublicoMvcController;
    @InjectMocks private ClienteMvcController clienteMvcController;
    @InjectMocks private CompraMvcController compraMvcController;
    @InjectMocks private ContrasenaMvcController contrasenaMvcController;
    @InjectMocks private DetalleCompraMvcController detalleCompraMvcController;
    @InjectMocks private DetalleVentaMvcController detalleVentaMvcController;
    @InjectMocks private InicioMvcController inicioMvcController;
    @InjectMocks private LoginMvcController loginMvcController;
    @InjectMocks private ProductoMvcController productoMvcController;
    @InjectMocks private ProveedorMvcController proveedorMvcController;
    @InjectMocks private UsuarioMvcController usuarioMvcController;
    @InjectMocks private VentaMvcController ventaMvcController;

    @Test
    void paginasDeInicioYLoginDevuelvenSusVistas() {
        assertEquals("index", inicioMvcController.inicio());
        assertEquals("login", loginMvcController.iniciarSesion());
    }

    @Test
    void adviceExponeCorreoYAutoridadDeAdministrador() {
        VistaGlobalAdvice advice = new VistaGlobalAdvice();
        assertEquals("", advice.correoSesion(null));
        assertFalse(advice.esAdministrador(null));
        when(authentication.getName()).thenReturn("ana@example.com");
        org.mockito.Mockito.doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
        assertEquals("ana@example.com", advice.correoSesion(authentication));
        assertTrue(advice.esAdministrador(authentication));
    }

    @Test
    void detalleMvcCargaIdYDetallesEnElModelo() {
        var modeloVenta = new ExtendedModelMap();
        when(detalleVentaDAO.listarPorVenta(8)).thenReturn(List.of());
        assertEquals("detalles-venta", detalleVentaMvcController.listar(8, modeloVenta));
        assertEquals(8, modeloVenta.getAttribute("idVenta"));
        assertEquals(List.of(), modeloVenta.getAttribute("detalles"));

        var modeloCompra = new ExtendedModelMap();
        when(detalleCompraDAO.listarPorCompra(9)).thenReturn(List.of());
        assertEquals("detalles-compra", detalleCompraMvcController.listar(9, modeloCompra));
        assertEquals(9, modeloCompra.getAttribute("idCompra"));
        assertEquals(List.of(), modeloCompra.getAttribute("detalles"));
    }

    @Test
    void clienteMvcListaValidaCreaYActualiza() {
        var modelo = new ExtendedModelMap();
        when(clienteDAO.listar()).thenReturn(List.of());
        assertEquals("clientes", clienteMvcController.listar(modelo));
        assertEquals(List.of(), modelo.getAttribute("clientes"));
        assertTrue(modelo.getAttribute("nuevoCliente") instanceof ClienteVO);

        var atributos = new RedirectAttributesModelMap();
        ClienteVO clienteInvalido = new ClienteVO(null, "Ana", "123", "Calle 1");
        assertEquals("redirect:/web/clientes", clienteMvcController.crear(clienteInvalido, atributos));
        assertFalse(atributos.getFlashAttributes().containsKey("exitoCliente"));

        atributos = new RedirectAttributesModelMap();
        ClienteVO cliente = new ClienteVO(null, " Ana ", " 12345678 ", " Calle 1 ");
        assertEquals("redirect:/web/clientes", clienteMvcController.crear(cliente, atributos));
        assertEquals("Ana", cliente.getNombre());
        assertEquals("12345678", cliente.getTelefono());
        assertEquals("Calle 1", cliente.getDireccion());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoCliente"));
        verify(clienteDAO).crear(cliente);

        atributos = new RedirectAttributesModelMap();
        when(clienteDAO.actualizar(cliente)).thenReturn(true, false);
        assertEquals("redirect:/web/clientes", clienteMvcController.actualizar(4, cliente, atributos));
        assertEquals(4, cliente.getIdCliente());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoCliente"));
        atributos = new RedirectAttributesModelMap();
        assertEquals("redirect:/web/clientes", clienteMvcController.actualizar(5, cliente, atributos));
        assertTrue(atributos.getFlashAttributes().containsKey("errorCliente"));
    }

    @Test
    void productoMvcIncluyeCategoriasYAdministraValidacionYEliminacion() {
        var modelo = new ExtendedModelMap();
        ProductoVO existente = new ProductoVO(1, "Agua", "Bebidas", "Marca", BigDecimal.ONE, 4);
        when(productoDAO.listar()).thenReturn(List.of(existente));
        assertEquals("productos", productoMvcController.listar(modelo));
        @SuppressWarnings("unchecked")
        List<String> categorias = (List<String>) modelo.getAttribute("categorias");
        assertTrue(categorias.contains("Bebidas"));
        assertEquals(1, categorias.stream().filter("Bebidas"::equalsIgnoreCase).count());

        var atributos = new RedirectAttributesModelMap();
        ProductoVO invalido = new ProductoVO(null, "  ", "Bebidas", "Marca", BigDecimal.ONE, 0);
        assertEquals("redirect:/web/productos", productoMvcController.crear(invalido, atributos));
        assertTrue(atributos.getFlashAttributes().containsKey("errorProducto"));

        atributos = new RedirectAttributesModelMap();
        ProductoVO producto = new ProductoVO(null, " Agua ", " Bebidas ", " Marca ", BigDecimal.ONE, 0);
        assertEquals("redirect:/web/productos", productoMvcController.crear(producto, atributos));
        assertEquals("Agua", producto.getNombre());
        assertEquals("Bebidas", producto.getCategoria());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoProducto"));
        verify(productoDAO).crear(producto);

        atributos = new RedirectAttributesModelMap();
        when(productoDAO.actualizar(producto)).thenReturn(true, false);
        productoMvcController.actualizar(1, producto, atributos);
        assertEquals(1, producto.getIdProducto());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoProducto"));
        atributos = new RedirectAttributesModelMap();
        productoMvcController.actualizar(2, producto, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorProducto"));

        atributos = new RedirectAttributesModelMap();
        when(productoDAO.eliminar(1)).thenReturn(true, false);
        productoMvcController.eliminar(1, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("exitoProducto"));
        atributos = new RedirectAttributesModelMap();
        productoMvcController.eliminar(1, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorProducto"));
        atributos = new RedirectAttributesModelMap();
        when(productoDAO.eliminar(3)).thenThrow(new DataIntegrityViolationException("referenciado"));
        productoMvcController.eliminar(3, atributos);
        assertTrue(atributos.getFlashAttributes().get("errorProducto").toString().contains("No se puede eliminar"));
    }

    @Test
    void proveedorMvcValidaYTrataErrorDeIntegridad() {
        var modelo = new ExtendedModelMap();
        when(proveedorDAO.listar()).thenReturn(List.of());
        assertEquals("proveedores", proveedorMvcController.listar(modelo));
        assertTrue(modelo.getAttribute("nuevoProveedor") instanceof ProveedorVO);

        var atributos = new RedirectAttributesModelMap();
        ProveedorVO proveedor = new ProveedorVO();
        proveedor.setNombre("Proveedor");
        proveedor.setTelefono("123");
        proveedor.setDireccion("Calle");
        proveedorMvcController.crear(proveedor, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorProveedor"));

        atributos = new RedirectAttributesModelMap();
        proveedor.setTelefono(" 12345678 ");
        proveedorMvcController.crear(proveedor, atributos);
        assertEquals("12345678", proveedor.getTelefono());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoProveedor"));

        atributos = new RedirectAttributesModelMap();
        when(proveedorDAO.actualizar(proveedor)).thenReturn(true);
        proveedorMvcController.actualizar(6, proveedor, atributos);
        assertEquals(6, proveedor.getIdProveedor());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoProveedor"));
        atributos = new RedirectAttributesModelMap();
        when(proveedorDAO.eliminar(6)).thenThrow(new DataIntegrityViolationException("referenciado"));
        proveedorMvcController.eliminar(6, atributos);
        assertTrue(atributos.getFlashAttributes().get("errorProveedor").toString().contains("No se puede eliminar"));
    }

    @Test
    void compraMvcRegistraYEliminaConExitoOErrorDeNegocio() {
        var modelo = new ExtendedModelMap();
        when(compraDAO.listar()).thenReturn(List.of());
        when(proveedorDAO.listar()).thenReturn(List.of());
        when(productoDAO.listar()).thenReturn(List.of());
        assertEquals("compras", compraMvcController.listar(modelo));
        assertEquals(List.of(), modelo.getAttribute("compras"));

        when(authentication.getName()).thenReturn("operador");
        var atributos = new RedirectAttributesModelMap();
        when(registroOperacionService.crearCompra("operador", 2, List.of(3), List.of(4), List.of(BigDecimal.TEN)))
                .thenReturn(1);
        assertEquals("redirect:/web/compras", compraMvcController.crear(
                2, List.of(3), List.of(4), List.of(BigDecimal.TEN), authentication, atributos));
        assertTrue(atributos.getFlashAttributes().containsKey("exitoCompra"));

        atributos = new RedirectAttributesModelMap();
        when(registroOperacionService.eliminarCompra(5)).thenReturn(true, false);
        compraMvcController.eliminar(5, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("exitoCompra"));
        atributos = new RedirectAttributesModelMap();
        compraMvcController.eliminar(5, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorCompra"));
        atributos = new RedirectAttributesModelMap();
        when(registroOperacionService.eliminarCompra(6)).thenThrow(new IllegalArgumentException("No permitido"));
        compraMvcController.eliminar(6, atributos);
        assertEquals("No permitido", atributos.getFlashAttributes().get("errorCompra"));
    }

    @Test
    void ventaMvcRegistraPagaYElimina() {
        var modelo = new ExtendedModelMap();
        when(ventaDAO.listar()).thenReturn(List.of());
        when(ventaDAO.listarFiadosPendientes()).thenReturn(List.of());
        when(ventaDAO.totalFiadoPendiente()).thenReturn(BigDecimal.ZERO);
        when(clienteDAO.listar()).thenReturn(List.of());
        when(productoDAO.listar()).thenReturn(List.of());
        assertEquals("ventas", ventaMvcController.listar(modelo));
        assertEquals(BigDecimal.ZERO, modelo.getAttribute("totalFiadoPendiente"));

        when(authentication.getName()).thenReturn("operador");
        var atributos = new RedirectAttributesModelMap();
        ventaMvcController.crear(2, List.of(3), List.of(1), true, authentication, atributos);
        verify(registroOperacionService).crearVenta("operador", 2, List.of(3), List.of(1), true);
        assertTrue(atributos.getFlashAttributes().containsKey("exitoVenta"));
        atributos = new RedirectAttributesModelMap();
        when(registroOperacionService.crearVenta("operador", 2, List.of(4), List.of(1), false))
                .thenThrow(new IllegalArgumentException("Sin inventario"));
        ventaMvcController.crear(2, List.of(4), List.of(1), false, authentication, atributos);
        assertEquals("Sin inventario", atributos.getFlashAttributes().get("errorVenta"));

        atributos = new RedirectAttributesModelMap();
        when(ventaDAO.marcarPagado(8)).thenReturn(true, false);
        ventaMvcController.marcarPagado(8, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("exitoVenta"));
        atributos = new RedirectAttributesModelMap();
        ventaMvcController.marcarPagado(8, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorVenta"));

        atributos = new RedirectAttributesModelMap();
        when(registroOperacionService.eliminarVenta(8)).thenReturn(true, false);
        ventaMvcController.eliminar(8, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("exitoVenta"));
        atributos = new RedirectAttributesModelMap();
        ventaMvcController.eliminar(8, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorVenta"));
    }

    @Test
    void usuarioMvcValidaCreacionYProtegeCuentaActiva() {
        var modelo = new ExtendedModelMap();
        when(usuarioDAO.listar()).thenReturn(List.of());
        assertEquals("usuarios", usuarioMvcController.listar(modelo));
        assertNull(modelo.getAttribute("usuarioEditar"));

        var atributos = new RedirectAttributesModelMap();
        UsuarioVO usuario = new UsuarioVO(null, " Ana ", " ana@example.com ", "contrasena1", "empleado");
        when(usuarioDAO.consultarPorCorreo("ana@example.com")).thenReturn(Optional.empty());
        usuarioMvcController.crear(usuario, atributos);
        assertEquals("Ana", usuario.getNombre());
        assertEquals("ana@example.com", usuario.getCorreo());
        assertEquals("EMPLEADO", usuario.getRol());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoUsuario"));

        atributos = new RedirectAttributesModelMap();
        usuarioMvcController.crear(new UsuarioVO(null, "Ana", "correo-malo", "contrasena1", "EMPLEADO"), atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorUsuario"));

        atributos = new RedirectAttributesModelMap();
        UsuarioVO existente = new UsuarioVO(4, "Ana", "ana@example.com", "hash", "ADMINISTRADOR");
        when(usuarioDAO.consultarPorId(4)).thenReturn(Optional.of(existente));
        when(authentication.getName()).thenReturn("ana@example.com");
        UsuarioVO cambios = new UsuarioVO(null, "Ana", "otro@example.com", null, "EMPLEADO");
        assertEquals("redirect:/web/usuarios/4/editar",
                usuarioMvcController.actualizar(4, cambios, authentication, atributos));
        assertTrue(atributos.getFlashAttributes().containsKey("errorUsuario"));

        atributos = new RedirectAttributesModelMap();
        assertEquals("redirect:/web/usuarios",
                usuarioMvcController.actualizarRol(4, "EMPLEADO", authentication, atributos));
        assertTrue(atributos.getFlashAttributes().containsKey("errorUsuario"));
        atributos = new RedirectAttributesModelMap();
        usuarioMvcController.eliminar(4, authentication, atributos);
        assertTrue(atributos.getFlashAttributes().containsKey("errorUsuario"));
    }

    @Test
    void usuarioMvcMuestraEdicionYActualizaDatosDeOtraCuenta() {
        UsuarioVO existente = new UsuarioVO(7, "Luis", "luis@example.com", "hash", "EMPLEADO");
        when(usuarioDAO.consultarPorId(7)).thenReturn(Optional.of(existente));
        when(usuarioDAO.consultarPorCorreo("nuevo@example.com")).thenReturn(Optional.empty());
        when(usuarioDAO.actualizarDatosBasicos(existente)).thenReturn(true);
        var modelo = new ExtendedModelMap();
        assertEquals("usuarios", usuarioMvcController.mostrarEdicion(7, modelo, new RedirectAttributesModelMap()));
        assertEquals(existente, modelo.getAttribute("usuarioEditar"));

        when(authentication.getName()).thenReturn("admin@example.com");
        var atributos = new RedirectAttributesModelMap();
        UsuarioVO cambios = new UsuarioVO(null, " Luis ", "nuevo@example.com", null, " administrador ");
        usuarioMvcController.actualizar(7, cambios, authentication, atributos);
        assertEquals("Luis", existente.getNombre());
        assertEquals("nuevo@example.com", existente.getCorreo());
        assertEquals("ADMINISTRADOR", existente.getRol());
        assertTrue(atributos.getFlashAttributes().containsKey("exitoUsuario"));
    }

    @Test
    void cambioContrasenaAutenticadoValidaYCambiaLaContrasena() {
        assertEquals("contrasena", contrasenaMvcController.mostrarFormulario());
        UsuarioVO usuario = new UsuarioVO(1, "Ana", "ana@example.com", "hash", "ADMINISTRADOR");
        when(authentication.getName()).thenReturn("ana@example.com");
        when(usuarioDAO.consultarPorCorreo("ana@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("actual", "hash")).thenReturn(true);
        when(passwordEncoder.matches("nueva123", "hash")).thenReturn(false);
        when(passwordEncoder.encode("nueva123")).thenReturn("hash-nuevo");
        when(usuarioDAO.actualizarContrasenaPorCorreo("ana@example.com", "hash-nuevo")).thenReturn(true);
        var model = new ExtendedModelMap();
        var attributes = new RedirectAttributesModelMap();
        assertEquals("redirect:/web/contrasena", contrasenaMvcController.cambiarContrasena(
                "actual", "nueva123", "nueva123", authentication, model, attributes));
        assertTrue(attributes.getFlashAttributes().containsKey("exitoContrasena"));
        assertFalse(model.containsAttribute("errorContrasena"));

        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);
        assertEquals("contrasena", contrasenaMvcController.cambiarContrasena(
                "incorrecta", "nueva123", "nueva123", authentication, model, attributes));
        assertTrue(model.containsAttribute("errorContrasena"));
    }

    @Test
    void cambioContrasenaPublicoValidaYActualiza() {
        assertEquals("cambiar-contrasena", cambioContrasenaPublicoMvcController.mostrarFormulario());
        UsuarioVO usuario = new UsuarioVO(1, "Ana", "ana@example.com", "hash", "ADMINISTRADOR");
        when(usuarioDAO.consultarPorCorreo("ana@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("actual", "hash")).thenReturn(true);
        when(passwordEncoder.matches("nueva123", "hash")).thenReturn(false);
        when(passwordEncoder.encode("nueva123")).thenReturn("hash-nuevo");
        when(usuarioDAO.actualizarContrasenaPorCorreo("ana@example.com", "hash-nuevo")).thenReturn(true);
        var model = new ExtendedModelMap();
        var attributes = new RedirectAttributesModelMap();
        assertEquals("redirect:/cambiar-contrasena", cambioContrasenaPublicoMvcController.cambiarContrasena(
                " ana@example.com ", "actual", "nueva123", "nueva123", model, attributes));
        assertTrue(attributes.getFlashAttributes().containsKey("exitoContrasena"));

        assertEquals("cambiar-contrasena", cambioContrasenaPublicoMvcController.cambiarContrasena(
                "desconocido@example.com", "actual", "nueva123", "nueva123", model, attributes));
        assertTrue(model.containsAttribute("errorContrasena"));
    }
}

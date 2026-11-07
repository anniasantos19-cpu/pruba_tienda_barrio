package com.Tienda.Tienda_de_Barrio.controller.res;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.CompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleCompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleVentaDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.VentaDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ClienteVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.CompraVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.DetalleCompraVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.DetalleVentaVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProductoVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.ProveedorVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.UsuarioVO;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.VentaVO;
import com.Tienda.Tienda_de_Barrio.service.RegistroOperacionService;

@ExtendWith(MockitoExtension.class)
class RestControllerUnitTests {

    @Mock private ClienteDAO clienteDAO;
    @Mock private CompraDAO compraDAO;
    @Mock private DetalleCompraDAO detalleCompraDAO;
    @Mock private DetalleVentaDAO detalleVentaDAO;
    @Mock private ProductoDAO productoDAO;
    @Mock private ProveedorDAO proveedorDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private VentaDAO ventaDAO;
    @Mock private RegistroOperacionService registroOperacionService;

    @InjectMocks private ClienteController clienteController;
    @InjectMocks private CompraController compraController;
    @InjectMocks private DetalleCompraController detalleCompraController;
    @InjectMocks private DetalleVentaController detalleVentaController;
    @InjectMocks private ProductoController productoController;
    @InjectMocks private ProveedorController proveedorController;
    @InjectMocks private UsuarioController usuarioController;
    @InjectMocks private VentaController ventaController;

    @Test
    void clienteRespondeConCreacionConsultaListadoYActualizacion() {
        ClienteVO cliente = new ClienteVO();
        when(clienteDAO.crear(cliente)).thenReturn(12);
        var creado = clienteController.crear(cliente);
        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        assertEquals(URI.create("/api/clientes/12"), creado.getHeaders().getLocation());
        assertSame(cliente, creado.getBody());

        when(clienteDAO.consultarPorId(12)).thenReturn(Optional.of(cliente));
        assertEquals(HttpStatus.OK, clienteController.consultarPorId(12).getStatusCode());
        when(clienteDAO.consultarPorId(13)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, clienteController.consultarPorId(13).getStatusCode());

        when(clienteDAO.listar()).thenReturn(List.of(cliente));
        assertEquals(List.of(cliente), clienteController.listar());

        when(clienteDAO.actualizar(cliente)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, clienteController.actualizar(12, cliente).getStatusCode());
        assertEquals(12, cliente.getIdCliente());
        assertEquals(HttpStatus.NOT_FOUND, clienteController.actualizar(13, cliente).getStatusCode());

        when(clienteDAO.eliminar(12)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, clienteController.eliminar(12).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, clienteController.eliminar(12).getStatusCode());
    }

    @Test
    void productoExponeConsultasYMutaciones() {
        ProductoVO producto = new ProductoVO();
        when(productoDAO.crear(producto)).thenReturn(7);
        assertEquals(URI.create("/api/productos/7"), productoController.crear(producto).getHeaders().getLocation());
        when(productoDAO.consultarPorId(7)).thenReturn(Optional.of(producto));
        assertSame(producto, productoController.consultarPorId(7).getBody());
        when(productoDAO.listar()).thenReturn(List.of(producto));
        when(productoDAO.buscarPorCategoria("Bebidas")).thenReturn(List.of(producto));
        when(productoDAO.buscarPorNombre("agua")).thenReturn(List.of(producto));
        when(productoDAO.buscarConPocasUnidades(3)).thenReturn(List.of(producto));
        when(productoDAO.listarOrdenadosPorPrecio()).thenReturn(List.of(producto));
        assertEquals(List.of(producto), productoController.listar());
        assertEquals(List.of(producto), productoController.buscarPorCategoria("Bebidas"));
        assertEquals(List.of(producto), productoController.buscarPorNombre("agua"));
        assertEquals(List.of(producto), productoController.buscarConPocasUnidades(3));
        assertEquals(List.of(producto), productoController.listarOrdenadosPorPrecio());
        verify(productoDAO).buscarConPocasUnidades(3);

        when(productoDAO.actualizar(producto)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, productoController.actualizar(7, producto).getStatusCode());
        assertEquals(7, producto.getIdProducto());
        assertEquals(HttpStatus.NOT_FOUND, productoController.actualizar(8, producto).getStatusCode());
        when(productoDAO.eliminar(7)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, productoController.eliminar(7).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, productoController.eliminar(7).getStatusCode());
    }

    @Test
    void proveedorPermiteOperacionesCrud() {
        ProveedorVO proveedor = new ProveedorVO();
        when(proveedorDAO.crear(proveedor)).thenReturn(4);
        assertEquals(URI.create("/api/proveedores/4"), proveedorController.crear(proveedor).getHeaders().getLocation());
        when(proveedorDAO.consultarPorId(4)).thenReturn(Optional.of(proveedor));
        when(proveedorDAO.listar()).thenReturn(List.of(proveedor));
        assertSame(proveedor, proveedorController.consultarPorId(4).getBody());
        assertEquals(List.of(proveedor), proveedorController.listar());
        when(proveedorDAO.actualizar(proveedor)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, proveedorController.actualizar(4, proveedor).getStatusCode());
        assertEquals(4, proveedor.getIdProveedor());
        assertEquals(HttpStatus.NOT_FOUND, proveedorController.actualizar(5, proveedor).getStatusCode());
        when(proveedorDAO.eliminar(4)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, proveedorController.eliminar(4).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, proveedorController.eliminar(4).getStatusCode());
    }

    @Test
    void compraRespondeYDelegaEliminacionAlServicioDeOperaciones() {
        CompraVO compra = new CompraVO();
        when(compraDAO.crear(compra)).thenReturn(20);
        assertEquals(URI.create("/api/compras/20"), compraController.crear(compra).getHeaders().getLocation());
        when(compraDAO.consultarPorId(20)).thenReturn(Optional.of(compra));
        when(compraDAO.listar()).thenReturn(List.of(compra));
        assertSame(compra, compraController.consultarPorId(20).getBody());
        assertEquals(List.of(compra), compraController.listar());
        when(compraDAO.actualizar(compra)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, compraController.actualizar(20, compra).getStatusCode());
        assertEquals(20, compra.getIdCompra());
        assertEquals(HttpStatus.NOT_FOUND, compraController.actualizar(21, compra).getStatusCode());

        when(registroOperacionService.eliminarCompra(20)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, compraController.eliminar(20).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, compraController.eliminar(20).getStatusCode());
        when(registroOperacionService.eliminarCompra(22)).thenThrow(new IllegalArgumentException("Conflicto"));
        assertEquals(HttpStatus.CONFLICT, compraController.eliminar(22).getStatusCode());
    }

    @Test
    void ventaRespondeYDelegaEliminacionAlServicioDeOperaciones() {
        VentaVO venta = new VentaVO();
        when(ventaDAO.crear(venta)).thenReturn(9);
        assertEquals(URI.create("/api/ventas/9"), ventaController.crear(venta).getHeaders().getLocation());
        when(ventaDAO.consultarPorId(9)).thenReturn(Optional.of(venta));
        when(ventaDAO.listar()).thenReturn(List.of(venta));
        assertSame(venta, ventaController.consultarPorId(9).getBody());
        assertEquals(List.of(venta), ventaController.listar());
        when(ventaDAO.actualizar(venta)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, ventaController.actualizar(9, venta).getStatusCode());
        assertEquals(9, venta.getIdVenta());
        assertEquals(HttpStatus.NOT_FOUND, ventaController.actualizar(10, venta).getStatusCode());

        when(registroOperacionService.eliminarVenta(9)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, ventaController.eliminar(9).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, ventaController.eliminar(9).getStatusCode());
        when(registroOperacionService.eliminarVenta(11)).thenThrow(new IllegalArgumentException("Conflicto"));
        assertEquals(HttpStatus.CONFLICT, ventaController.eliminar(11).getStatusCode());
    }

    @Test
    void detallesDeCompraYVentaSoportanOperacionesYFiltradoPorCabecera() {
        DetalleCompraVO detalleCompra = new DetalleCompraVO();
        when(detalleCompraDAO.crear(detalleCompra)).thenReturn(3);
        assertEquals(URI.create("/api/detalles-compras/3"),
                detalleCompraController.crear(detalleCompra).getHeaders().getLocation());
        when(detalleCompraDAO.consultarPorId(3)).thenReturn(Optional.of(detalleCompra));
        when(detalleCompraDAO.listar()).thenReturn(List.of(detalleCompra));
        when(detalleCompraDAO.listarPorCompra(20)).thenReturn(List.of(detalleCompra));
        assertSame(detalleCompra, detalleCompraController.consultarPorId(3).getBody());
        assertEquals(List.of(detalleCompra), detalleCompraController.listar());
        assertEquals(List.of(detalleCompra), detalleCompraController.listarPorCompra(20));
        when(detalleCompraDAO.actualizar(detalleCompra)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, detalleCompraController.actualizar(3, detalleCompra).getStatusCode());
        assertEquals(3, detalleCompra.getIdDetalle());
        assertEquals(HttpStatus.NOT_FOUND, detalleCompraController.actualizar(4, detalleCompra).getStatusCode());
        when(detalleCompraDAO.eliminar(3)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, detalleCompraController.eliminar(3).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, detalleCompraController.eliminar(3).getStatusCode());

        DetalleVentaVO detalleVenta = new DetalleVentaVO();
        when(detalleVentaDAO.crear(detalleVenta)).thenReturn(5);
        assertEquals(URI.create("/api/detalles-ventas/5"),
                detalleVentaController.crear(detalleVenta).getHeaders().getLocation());
        when(detalleVentaDAO.consultarPorId(5)).thenReturn(Optional.of(detalleVenta));
        when(detalleVentaDAO.listar()).thenReturn(List.of(detalleVenta));
        when(detalleVentaDAO.listarPorVenta(30)).thenReturn(List.of(detalleVenta));
        assertSame(detalleVenta, detalleVentaController.consultarPorId(5).getBody());
        assertEquals(List.of(detalleVenta), detalleVentaController.listar());
        assertEquals(List.of(detalleVenta), detalleVentaController.listarPorVenta(30));
        when(detalleVentaDAO.actualizar(detalleVenta)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, detalleVentaController.actualizar(5, detalleVenta).getStatusCode());
        assertEquals(5, detalleVenta.getIdDetalle());
        assertEquals(HttpStatus.NOT_FOUND, detalleVentaController.actualizar(6, detalleVenta).getStatusCode());
        when(detalleVentaDAO.eliminar(5)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, detalleVentaController.eliminar(5).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, detalleVentaController.eliminar(5).getStatusCode());
    }

    @Test
    void usuarioDevuelveDatosPublicosSinContrasena() {
        UsuarioVO usuario = new UsuarioVO(6, "Ana", "ana@example.com", "secreta", "EMPLEADO");
        when(usuarioDAO.crear(usuario)).thenReturn(6);
        var creado = usuarioController.crear(usuario);
        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        assertEquals(URI.create("/api/usuarios/6"), creado.getHeaders().getLocation());
        Object respuestaCreada = creado.getBody();
        assertTrue(respuestaCreada.toString().contains("correo=ana@example.com"));
        assertFalse(respuestaCreada.toString().contains("secreta"));
        when(usuarioDAO.consultarPorId(6)).thenReturn(Optional.of(usuario));
        var consultado = usuarioController.consultarPorId(6);
        assertEquals(HttpStatus.OK, consultado.getStatusCode());
        Object respuestaConsultada = consultado.getBody();
        assertTrue(respuestaConsultada.toString().contains("nombre=Ana"));
        when(usuarioDAO.consultarPorId(7)).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.consultarPorId(7).getStatusCode());
        when(usuarioDAO.listar()).thenReturn(List.of(usuario));
        Object respuestaListado = usuarioController.listar().get(0);
        assertTrue(respuestaListado.toString().contains("correo=ana@example.com"));
        assertFalse(respuestaListado.toString().contains("secreta"));
        when(usuarioDAO.actualizar(usuario)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, usuarioController.actualizar(6, usuario).getStatusCode());
        assertEquals(6, usuario.getIdUsuario());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.actualizar(7, usuario).getStatusCode());
        when(usuarioDAO.eliminar(6)).thenReturn(true, false);
        assertEquals(HttpStatus.NO_CONTENT, usuarioController.eliminar(6).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, usuarioController.eliminar(6).getStatusCode());
    }
}

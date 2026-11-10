
package com.Tienda.Tienda_de_Barrio.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.*;
import com.Tienda.Tienda_de_Barrio.Modelo.VO.*;

@ExtendWith(MockitoExtension.class)
class RegistroOperacionServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private ProveedorDAO proveedorDAO;

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private VentaDAO ventaDAO;

    @Mock
    private CompraDAO compraDAO;

    @Mock
    private DetalleVentaDAO detalleVentaDAO;

    @Mock
    private DetalleCompraDAO detalleCompraDAO;

    @InjectMocks
    private RegistroOperacionService servicio;

    private ProductoVO producto;

    @BeforeEach
    void configurar() {
        producto = new ProductoVO();
        producto.setIdProducto(10);
        producto.setNombre("Arroz");
        producto.setPrecio(new BigDecimal("3000"));
        producto.setCantidad(20);
    }

    @Test
    void crearVenta_ConDatosValidos_RegistraVenta() {
        UsuarioVO usuario = new UsuarioVO(1, "Empleado",
                "empleado@tienda.com", "123", "EMPLEADO");

        when(usuarioDAO.consultarPorCorreo("empleado@tienda.com"))
                .thenReturn(Optional.of(usuario));
        when(clienteDAO.consultarPorId(2))
                .thenReturn(Optional.of(new ClienteVO()));
        when(productoDAO.consultarPorId(10))
                .thenReturn(Optional.of(producto));
        when(productoDAO.descontarExistencias(10, 2))
                .thenReturn(true);

        doAnswer(invocacion -> {
            VentaVO venta = invocacion.getArgument(0);
            venta.setIdVenta(100);
            return 100;
        }).when(ventaDAO).crear(any(VentaVO.class));

        int resultado = servicio.crearVenta(
                "empleado@tienda.com", 2,
                List.of(10), List.of(2), false);

        assertEquals(100, resultado);
        verify(productoDAO).descontarExistencias(10, 2);
        verify(ventaDAO).crear(any(VentaVO.class));
        verify(detalleVentaDAO).crear(any(DetalleVentaVO.class));
    }

    @Test
    void crearVenta_SinExistencias_LanzaExcepcion() {
        UsuarioVO usuario = new UsuarioVO(1, "Empleado",
                "empleado@tienda.com", "123", "EMPLEADO");

        when(usuarioDAO.consultarPorCorreo("empleado@tienda.com"))
                .thenReturn(Optional.of(usuario));
        when(clienteDAO.consultarPorId(2))
                .thenReturn(Optional.of(new ClienteVO()));
        when(productoDAO.consultarPorId(10))
                .thenReturn(Optional.of(producto));
        when(productoDAO.descontarExistencias(10, 5))
                .thenReturn(false);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.crearVenta(
                        "empleado@tienda.com", 2,
                        List.of(10), List.of(5), false));

        assertTrue(error.getMessage().contains("No hay existencias"));
        verify(ventaDAO, never()).crear(any(VentaVO.class));
    }

    @Test
    void crearCompra_ConDatosValidos_RegistraCompra() {
        UsuarioVO usuario = new UsuarioVO(1, "Empleado",
                "empleado@tienda.com", "123", "EMPLEADO");

        when(usuarioDAO.consultarPorCorreo("empleado@tienda.com"))
                .thenReturn(Optional.of(usuario));
        when(proveedorDAO.consultarPorId(3))
                .thenReturn(Optional.of(new ProveedorVO()));
        when(productoDAO.consultarPorId(10))
                .thenReturn(Optional.of(producto));
        when(productoDAO.aumentarExistencias(10, 5))
                .thenReturn(true);

        doAnswer(invocacion -> {
            CompraVO compra = invocacion.getArgument(0);
            compra.setIdCompra(200);
            return 200;
        }).when(compraDAO).crear(any(CompraVO.class));

        int resultado = servicio.crearCompra(
                "empleado@tienda.com", 3,
                List.of(10), List.of(5),
                List.of(new BigDecimal("2500")));

        assertEquals(200, resultado);
        verify(productoDAO).aumentarExistencias(10, 5);
        verify(compraDAO).crear(any(CompraVO.class));
        verify(detalleCompraDAO).crear(any(DetalleCompraVO.class));
    }

    @Test
    void crearCompra_ConPrecioInvalido_LanzaExcepcion() {
        UsuarioVO usuario = new UsuarioVO(1, "Empleado",
                "empleado@tienda.com", "123", "EMPLEADO");

        when(usuarioDAO.consultarPorCorreo("empleado@tienda.com"))
                .thenReturn(Optional.of(usuario));
        when(proveedorDAO.consultarPorId(3))
                .thenReturn(Optional.of(new ProveedorVO()));
        when(productoDAO.consultarPorId(10))
                .thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.crearCompra(
                        "empleado@tienda.com", 3,
                        List.of(10), List.of(5),
                        List.of(BigDecimal.ZERO)));

        verify(compraDAO, never()).crear(any(CompraVO.class));
    }
}

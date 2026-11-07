package com.Tienda.Tienda_de_Barrio.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ClienteDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.CompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleCompraDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.DetalleVentaDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProductoDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.ProveedorDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.UsuarioDAO;
import com.Tienda.Tienda_de_Barrio.Modelo.DAO.VentaDAO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.CompraVO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.DetalleCompraVO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.DetalleVentaVO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.ProductoVO;
import com.Tienda.Tienda_de_Barrio.modelo.VO.VentaVO;

@Service
public class RegistroOperacionService {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private final ProveedorDAO proveedorDAO;
    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;
    private final CompraDAO compraDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final DetalleCompraDAO detalleCompraDAO;

    public RegistroOperacionService(UsuarioDAO usuarioDAO, ClienteDAO clienteDAO, ProveedorDAO proveedorDAO,
            ProductoDAO productoDAO, VentaDAO ventaDAO, CompraDAO compraDAO, DetalleVentaDAO detalleVentaDAO,
            DetalleCompraDAO detalleCompraDAO) {
        this.usuarioDAO = usuarioDAO;
        this.clienteDAO = clienteDAO;
        this.proveedorDAO = proveedorDAO;
        this.productoDAO = productoDAO;
        this.ventaDAO = ventaDAO;
        this.compraDAO = compraDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.detalleCompraDAO = detalleCompraDAO;
    }

    @Transactional
    public int crearVenta(String correoUsuario, Integer idCliente, List<Integer> idsProducto,
            List<Integer> cantidades, boolean fiado) {
        int idUsuario = usuarioDAO.consultarPorCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta del empleado.")).getIdUsuario();
        if (clienteDAO.consultarPorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException("Selecciona un cliente válido.");
        }
        Map<Integer, Integer> lineas = consolidarLineas(idsProducto, cantidades);
        List<DetalleVentaVO> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> linea : lineas.entrySet()) {
            ProductoVO producto = buscarProducto(linea.getKey());
            BigDecimal precio = producto.getPrecio();
            if (precio == null || precio.signum() <= 0) {
                throw new IllegalArgumentException("El precio de un producto no es válido.");
            }
            if (producto.getCantidad() == null || producto.getCantidad() < linea.getValue()
                    || !productoDAO.descontarExistencias(producto.getIdProducto(), linea.getValue())) {
                throw new IllegalArgumentException("No hay existencias suficientes de " + producto.getNombre() + ".");
            }
            total = total.add(precio.multiply(BigDecimal.valueOf(linea.getValue())));
            detalles.add(new DetalleVentaVO(null, null, producto.getIdProducto(), linea.getValue(), precio));
        }

        VentaVO venta = new VentaVO(null, idUsuario, idCliente, LocalDateTime.now(), total);
        venta.setFiado(fiado);
        venta.setPagado(!fiado);
        ventaDAO.crear(venta);
        for (DetalleVentaVO detalle : detalles) {
            detalle.setIdVenta(venta.getIdVenta());
            detalleVentaDAO.crear(detalle);
        }
        return venta.getIdVenta();
    }

    @Transactional
    public int crearCompra(String correoUsuario, Integer idProveedor, List<Integer> idsProducto,
            List<Integer> cantidades, List<BigDecimal> precios) {
        int idUsuario = usuarioDAO.consultarPorCorreo(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta del empleado.")).getIdUsuario();
        if (proveedorDAO.consultarPorId(idProveedor).isEmpty()) {
            throw new IllegalArgumentException("Selecciona un proveedor válido.");
        }
        validarLineas(idsProducto, cantidades);
        if (precios == null || precios.size() != idsProducto.size()) {
            throw new IllegalArgumentException("Completa cantidad y precio de cada producto.");
        }

        List<DetalleCompraVO> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (int indice = 0; indice < idsProducto.size(); indice++) {
            int idProducto = idsProducto.get(indice);
            int cantidad = cantidades.get(indice);
            BigDecimal precio = precios.get(indice);
            ProductoVO producto = buscarProducto(idProducto);
            if (precio == null || precio.signum() <= 0) {
                throw new IllegalArgumentException("Ingresa un precio de compra válido para cada producto.");
            }
            total = total.add(precio.multiply(BigDecimal.valueOf(cantidad)));
            detalles.add(new DetalleCompraVO(null, null, producto.getIdProducto(), cantidad, precio));
        }

        CompraVO compra = new CompraVO(null, idUsuario, idProveedor, LocalDateTime.now(), total);
        compraDAO.crear(compra);
        for (DetalleCompraVO detalle : detalles) {
            if (!productoDAO.aumentarExistencias(detalle.getIdProducto(), detalle.getCantidad())) {
                throw new IllegalArgumentException("No se encontró uno de los productos seleccionados.");
            }
            detalle.setIdCompra(compra.getIdCompra());
            detalleCompraDAO.crear(detalle);
        }
        return compra.getIdCompra();
    }

    @Transactional
    public boolean eliminarVenta(int idVenta) {
        List<DetalleVentaVO> detalles = detalleVentaDAO.listarPorVenta(idVenta);
        if (ventaDAO.consultarPorId(idVenta).isEmpty()) {
            return false;
        }
        for (DetalleVentaVO detalle : detalles) {
            if (!productoDAO.aumentarExistencias(detalle.getIdProducto(), detalle.getCantidad())) {
                throw new IllegalArgumentException("No se pudo restaurar el inventario de la venta.");
            }
        }
        detalleVentaDAO.eliminarPorVenta(idVenta);
        if (!ventaDAO.eliminar(idVenta)) {
            throw new IllegalArgumentException("No se pudo eliminar la venta seleccionada.");
        }
        return true;
    }

    @Transactional
    public boolean eliminarCompra(int idCompra) {
        List<DetalleCompraVO> detalles = detalleCompraDAO.listarPorCompra(idCompra);
        if (compraDAO.consultarPorId(idCompra).isEmpty()) {
            return false;
        }
        for (DetalleCompraVO detalle : detalles) {
            if (!productoDAO.retirarExistencias(detalle.getIdProducto(), detalle.getCantidad())) {
                throw new IllegalArgumentException("No hay existencias suficientes para revertir esta compra.");
            }
        }
        detalleCompraDAO.eliminarPorCompra(idCompra);
        if (!compraDAO.eliminar(idCompra)) {
            throw new IllegalArgumentException("No se pudo eliminar la compra seleccionada.");
        }
        return true;
    }

    private Map<Integer, Integer> consolidarLineas(List<Integer> idsProducto, List<Integer> cantidades) {
        validarLineas(idsProducto, cantidades);
        Map<Integer, Integer> lineas = new LinkedHashMap<>();
        try {
            for (int indice = 0; indice < idsProducto.size(); indice++) {
                lineas.merge(idsProducto.get(indice), cantidades.get(indice), Math::addExact);
            }
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("La cantidad total de un producto es demasiado grande.");
        }
        return lineas;
    }

    private void validarLineas(List<Integer> idsProducto, List<Integer> cantidades) {
        if (idsProducto == null || cantidades == null || idsProducto.isEmpty()
                || idsProducto.size() != cantidades.size()) {
            throw new IllegalArgumentException("Agrega al menos un producto e indica su cantidad.");
        }
        for (int indice = 0; indice < idsProducto.size(); indice++) {
            if (idsProducto.get(indice) == null || cantidades.get(indice) == null || cantidades.get(indice) < 1) {
                throw new IllegalArgumentException("Selecciona un producto e indica una cantidad válida.");
            }
        }
    }

    private ProductoVO buscarProducto(int idProducto) {
        return productoDAO.consultarPorId(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró uno de los productos seleccionados."));
    }
}

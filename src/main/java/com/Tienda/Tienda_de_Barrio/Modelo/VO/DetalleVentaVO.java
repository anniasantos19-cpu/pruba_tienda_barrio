package com.Tienda.Tienda_de_Barrio.Modelo.VO;

import java.math.BigDecimal;

public class DetalleVentaVO {
    private Integer idDetalle;
    private Integer idVenta;
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precio;

    public DetalleVentaVO() {
    }

    public DetalleVentaVO(Integer idDetalle, Integer idVenta, Integer idProducto, Integer cantidad, BigDecimal precio) {
        this.idDetalle = idDetalle;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}

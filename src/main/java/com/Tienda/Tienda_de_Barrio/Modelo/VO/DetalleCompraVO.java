package com.Tienda.Tienda_de_Barrio.Modelo.VO;

import java.math.BigDecimal;

public class DetalleCompraVO {
    private Integer idDetalle;
    private Integer idCompra;
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precio;

    public DetalleCompraVO() {
    }

    public DetalleCompraVO(Integer idDetalle, Integer idCompra, Integer idProducto, Integer cantidad,
            BigDecimal precio) {
        this.idDetalle = idDetalle;
        this.idCompra = idCompra;
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

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
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

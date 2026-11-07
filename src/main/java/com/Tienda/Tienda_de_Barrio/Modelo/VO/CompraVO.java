package com.Tienda.Tienda_de_Barrio.Modelo.VO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraVO {
    private Integer idCompra;
    private Integer idUsuario;
    private Integer idProveedor;
    private LocalDateTime fecha;
    private BigDecimal total;

    public CompraVO() {
    }

    public CompraVO(Integer idCompra, Integer idUsuario, Integer idProveedor, LocalDateTime fecha, BigDecimal total) {
        this.idCompra = idCompra;
        this.idUsuario = idUsuario;
        this.idProveedor = idProveedor;
        this.fecha = fecha;
        this.total = total;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

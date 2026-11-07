package com.Tienda.Tienda_de_Barrio.Modelo.VO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaVO {

    private Integer idVenta;
    private Integer idUsuario;
    private Integer idCliente;
    private LocalDateTime fecha;
    private BigDecimal total;
    private boolean fiado;
    private boolean pagado = true;
    private String nombreCliente;

    public VentaVO() {
    }

    public VentaVO(Integer idVenta, Integer idUsuario, Integer idCliente, LocalDateTime fecha, BigDecimal total) {
        this.idVenta = idVenta;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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

    public boolean isFiado() {
        return fiado;
    }

    public void setFiado(boolean fiado) {
        this.fiado = fiado;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}

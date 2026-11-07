package com.Tienda.Tienda_de_Barrio.modelo.VO;

import java.math.BigDecimal;

public class ProductoVO {
    private Integer idProducto;
    private String nombre;
    private String categoria;
    private String marca;
    private BigDecimal precio;
    private Integer cantidad;

    public ProductoVO() {
    }

    public ProductoVO(Integer idProducto, String nombre, String categoria, String marca, BigDecimal precio,
            Integer cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

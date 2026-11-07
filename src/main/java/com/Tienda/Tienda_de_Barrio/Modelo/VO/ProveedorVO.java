package com.Tienda.Tienda_de_Barrio.modelo.VO;

public class ProveedorVO {
    private Integer idProveedor;
    private String nombre;
    private String telefono;
    private String direccion;

    public ProveedorVO() {
    }

    public ProveedorVO(Integer idProveedor, String nombre, String telefono, String direccion) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

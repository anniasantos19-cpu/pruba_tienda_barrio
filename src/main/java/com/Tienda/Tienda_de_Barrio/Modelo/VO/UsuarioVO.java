package com.Tienda.Tienda_de_Barrio.modelo.VO;

public class UsuarioVO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;

    public UsuarioVO() {
    }

    public UsuarioVO(Integer idUsuario, String nombre, String correo, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "UsuarioVO[idUsuario=%s, nombre=%s, correo=%s, contrasena=[PROTEGIDA], rol=%s]"
                .formatted(idUsuario, nombre, correo, rol);
    }
}

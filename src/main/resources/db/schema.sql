CREATE DATABASE IF NOT EXISTS tienda_barrio
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tienda_barrio;

CREATE TABLE IF NOT EXISTS Usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(32) NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_correo UNIQUE (correo)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Clientes (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    direccion VARCHAR(255),
    PRIMARY KEY (id_cliente)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Proveedores (
    id_proveedor INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    direccion VARCHAR(255),
    PRIMARY KEY (id_proveedor)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Productos (
    id_producto INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(160) NOT NULL,
    categoria VARCHAR(120),
    marca VARCHAR(120),
    precio DECIMAL(12, 2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_producto),
    CONSTRAINT chk_productos_precio CHECK (precio >= 0),
    CONSTRAINT chk_productos_cantidad CHECK (cantidad >= 0),
    INDEX idx_productos_nombre (nombre),
    INDEX idx_productos_categoria (categoria)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Compras (
    id_compra INT NOT NULL AUTO_INCREMENT,
    id_usuario INT,
    id_proveedor INT,
    fecha DATETIME NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id_compra),
    INDEX idx_compras_usuario (id_usuario),
    INDEX idx_compras_proveedor (id_proveedor),
    INDEX idx_compras_fecha (fecha),
    CONSTRAINT fk_compras_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuarios (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_compras_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES Proveedores (id_proveedor)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Ventas (
    id_venta INT NOT NULL AUTO_INCREMENT,
    id_usuario INT,
    id_cliente INT,
    fecha DATETIME NOT NULL,
    total DECIMAL(12, 2) NOT NULL,
    es_fiado BOOLEAN NOT NULL DEFAULT FALSE,
    pagado BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_venta),
    INDEX idx_ventas_usuario (id_usuario),
    INDEX idx_ventas_cliente (id_cliente),
    INDEX idx_ventas_fecha (fecha),
    INDEX idx_ventas_fiado_pagado (es_fiado, pagado),
    CONSTRAINT fk_ventas_usuario
        FOREIGN KEY (id_usuario) REFERENCES Usuarios (id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_ventas_cliente
        FOREIGN KEY (id_cliente) REFERENCES Clientes (id_cliente)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Detalle_Compras (
    id_detalle INT NOT NULL AUTO_INCREMENT,
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id_detalle),
    INDEX idx_detalle_compras_compra (id_compra),
    INDEX idx_detalle_compras_producto (id_producto),
    CONSTRAINT chk_detalle_compras_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_compras_precio CHECK (precio >= 0),
    CONSTRAINT fk_detalle_compras_compra
        FOREIGN KEY (id_compra) REFERENCES Compras (id_compra)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_compras_producto
        FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS Detalle_Ventas (
    id_detalle INT NOT NULL AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id_detalle),
    INDEX idx_detalle_ventas_venta (id_venta),
    INDEX idx_detalle_ventas_producto (id_producto),
    CONSTRAINT chk_detalle_ventas_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_ventas_precio CHECK (precio >= 0),
    CONSTRAINT fk_detalle_ventas_venta
        FOREIGN KEY (id_venta) REFERENCES Ventas (id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_ventas_producto
        FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

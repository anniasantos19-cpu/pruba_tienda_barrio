package com.Tienda.Tienda_de_Barrio.modelo.VO;

import java.math.BigDecimal;

public record SaldoFiadoVO(int idCliente, String nombreCliente, BigDecimal saldoPendiente, int ventasPendientes) {

}

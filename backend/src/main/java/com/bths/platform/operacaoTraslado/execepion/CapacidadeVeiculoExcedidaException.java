package com.bths.platform.operacaoTraslado.execepion;

public class CapacidadeVeiculoExcedidaException
        extends RuntimeException {

  public CapacidadeVeiculoExcedidaException(
          String mensagem
  ) {
    super(mensagem);
  }
}

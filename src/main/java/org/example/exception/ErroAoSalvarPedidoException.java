package org.example.exception;

public class ErroAoSalvarPedidoException extends RuntimeException {
    public ErroAoSalvarPedidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
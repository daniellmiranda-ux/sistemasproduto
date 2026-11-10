package org.example.service;

import org.example.exception.ErroAoSalvarPedidoException;
import org.example.exception.EstoqueInsuficienteException;
import org.example.exception.ProdutoNaoEncontradoException;
import org.example.model.Pedido;
import org.example.model.Produto;
import org.example.repository.PedidoRepository;
import org.example.repository.ProdutoRepository;

public class PedidoService {
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public PedidoService(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido criarPedido(Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        Produto produto = produtoRepository.buscarPorId(produtoId);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado: " + produtoId);
        }
        if (quantidade > produto.getQuantidade()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente");
        }
        double total = produto.getPreco() * quantidade;
        Pedido pedido = new Pedido(produto, quantidade, total);
        try {
            pedidoRepository.salvar(pedido);
        } catch (RuntimeException e) {
            throw new ErroAoSalvarPedidoException("Falha ao salvar o pedido", e);
        }
        return pedido;
    }
}
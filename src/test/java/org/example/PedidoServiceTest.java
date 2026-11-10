package org.example;

import org.example.exception.ErroAoSalvarPedidoException;
import org.example.exception.EstoqueInsuficienteException;
import org.example.exception.ProdutoNaoEncontradoException;
import org.example.model.Pedido;
import org.example.model.Produto;
import org.example.repository.PedidoRepository;
import org.example.repository.ProdutoRepository;
import org.example.service.PedidoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService service;

    @Test
    @DisplayName("PED01/PED02 - Deve criar pedido com sucesso e calcular valor total")
    void deveCalcularValorTotalDoPedido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 2);

        assertNotNull(pedido);
        assertEquals(99.80, pedido.getValorTotal(), 0.001);
        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("PED03 - Não deve criar pedido com quantidade zero")
    void naoDeveCriarPedidoComQuantidadeZero() {
        assertThrows(IllegalArgumentException.class, () -> service.criarPedido(1L, 0));
        verify(produtoRepository, never()).buscarPorId(any());
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED04 - Não deve criar pedido com quantidade negativa")
    void naoDeveCriarPedidoComQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class, () -> service.criarPedido(1L, -3));
        verify(produtoRepository, never()).buscarPorId(any());
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED05 - Não deve criar pedido para produto inexistente")
    void naoDeveCriarPedidoQuandoProdutoNaoExistir() {
        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.criarPedido(99L, 2));
        verify(produtoRepository, times(1)).buscarPorId(99L);
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED06 - Não deve criar pedido quando estoque for insuficiente")
    void naoDeveCriarPedidoComEstoqueInsuficiente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class, () -> service.criarPedido(1L, 11));
        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED07 - Deve criar pedido com quantidade exatamente igual ao estoque")
    void deveCriarPedidoComQuantidadeIgualAoEstoque() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 10);

        assertNotNull(pedido);
        assertEquals(499.00, pedido.getValorTotal(), 0.001);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Atividade 7 Cenário A - Deve tratar erro do repositório ao salvar pedido")
    void deveTratarErroBancoAoSalvarPedido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        assertThrows(ErroAoSalvarPedidoException.class, () -> service.criarPedido(1L, 2));
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Atividade 7 Cenário C - Falha ao buscar produto não deve salvar pedido")
    void naoDeveSalvarPedidoSeFalharBuscaDoProduto() {
        when(produtoRepository.buscarPorId(1L)).thenThrow(new RuntimeException("Erro ao buscar"));

        assertThrows(RuntimeException.class, () -> service.criarPedido(1L, 2));
        verify(pedidoRepository, never()).salvar(any());
    }
}
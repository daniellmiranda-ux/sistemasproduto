package org.example;

import org.example.exception.ErroAoSalvarProdutoException;
import org.example.exception.ProdutoNaoEncontradoException;
import org.example.model.Produto;
import org.example.repository.ProdutoRepository;
import org.example.service.ProdutoService;
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
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    @DisplayName("CT01 - Deve salvar produto com dados válidos")
    void ct01_deveSalvarProdutoComDadosValidos() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertNotNull(resultado);
        assertEquals("Mouse", resultado.getNome());
        assertEquals(49.90, resultado.getPreco());
        assertEquals(10, resultado.getQuantidade());
        verify(repository, times(1)).salvar(produto);
    }

    @Test
    @DisplayName("CT02 - Não deve salvar produto com preço negativo")
    void ct02_naoDeveSalvarProdutoComPrecoNegativo() {
        Produto produto = new Produto("Mouse", -10.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT03 - Não deve salvar produto com preço igual a zero")
    void ct03_naoDeveSalvarProdutoComPrecoZero() {
        Produto produto = new Produto("Teclado", 0.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT04 - Não deve salvar produto com nome vazio")
    void ct04_naoDeveSalvarProdutoComNomeVazio() {
        Produto produto = new Produto("", 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT05 - Não deve salvar produto com nome nulo")
    void ct05_naoDeveSalvarProdutoComNomeNulo() {
        Produto produto = new Produto(null, 100.0, 5);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT06 - Não deve salvar produto com quantidade negativa")
    void ct06_naoDeveSalvarProdutoComQuantidadeNegativa() {
        Produto produto = new Produto("Monitor", 500.0, -1);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(produto));
        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT07 - Deve salvar produto com estoque zerado")
    void ct07_deveSalvarProdutoComEstoqueZerado() {
        Produto produto = new Produto("Gabinete", 200.0, 0);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        assertNotNull(resultado);
        assertEquals(0, resultado.getQuantidade());
        verify(repository, times(1)).salvar(produto);
    }

    @Test
    @DisplayName("CT08 - Deve buscar produto existente por ID")
    void ct08_deveBuscarProdutoExistente() {
        Produto produto = new Produto("Fone de Ouvido", 150.0, 8);
        when(repository.buscarPorId(1L)).thenReturn(produto);

        Produto resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Fone de Ouvido", resultado.getNome());
        verify(repository, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("CT09 - Deve lançar exceção ao buscar produto inexistente")
    void ct09_deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class, () -> service.buscarPorId(99L));
        verify(repository, times(1)).buscarPorId(99L);
    }

    @Test
    @DisplayName("CT10 - Deve lançar ErroAoSalvarProdutoException quando o repository falhar")
    void ct10_deveLancarErroQuandoRepositoryFalharAoSalvar() {
        Produto produto = new Produto("Teclado", 129.90, 5);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(repository).salvar(any(Produto.class));

        assertThrows(ErroAoSalvarProdutoException.class, () -> service.salvar(produto));
        verify(repository, times(1)).salvar(any(Produto.class));
    }
}
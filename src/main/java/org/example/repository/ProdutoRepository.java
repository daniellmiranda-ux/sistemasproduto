package org.example.repository;

import org.example.model.Produto;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    Produto buscarPorId(Long id);
}
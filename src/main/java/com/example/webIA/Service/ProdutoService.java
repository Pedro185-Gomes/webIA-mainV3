package com.example.webIA.Service;

import com.example.webIA.Model.Produto;
import com.example.webIA.Repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public String buscarProdutos(String termo) {
        // divide a mensagem em palavras e busca cada uma
        String[] palavras = termo.split("\\s+");
        List<Produto> todos = new ArrayList<>();

        for (String palavra : palavras) {
            if (palavra.length() < 3) continue; // ignora palavras muito curtas
            List<Produto> encontrados = produtoRepository.buscarPorTermoComEstoque(palavra);
            todos.addAll(encontrados);
        }

        // remove duplicatas por ID
        List<Produto> unicos = todos.stream()
                .collect(Collectors.toMap(Produto::getId, p -> p, (a, b) -> a))
                .values()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        if (unicos.isEmpty()) return null;

        StringBuilder sb = new StringBuilder("Produtos encontrados no catálogo:\n");
        for (Produto p : unicos) {
            String preco = (p.getPrecoVenda() == null || p.getPrecoVenda() <= 0.01)
                    ? "Preço sob consulta"
                    : String.format("R$ %.2f", p.getPrecoVenda());
            String estoque = (p.getQtdAtual() == null || p.getQtdAtual() == 0)
                    ? "Sob consulta"
                    : p.getQtdAtual() + " unidades";
            sb.append(String.format("- [%d] %s | %s | Estoque: %s\n",
                    p.getId(), p.getDescricao(), preco, estoque));
        }
        return sb.toString();
    }
}



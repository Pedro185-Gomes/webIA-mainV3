package com.example.webIA.Service;

import com.example.webIA.Model.Produto;
import com.example.webIA.Repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogoImportProdutosService {

    private final ProdutoRepository produtoRepository;

    public void importar(String caminhoArquivo) {
        log.info("Iniciando importação do catálogo: {}", caminhoArquivo);
        int erros = 0;
        List<Produto> produtos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] campos = linha.split(";");
                if (campos.length < 3) continue;

                try {
                    Long id = Long.parseLong(campos[0].trim());
                    String descricao = campos[1].trim();
                    String prcVendaStr = campos[2].trim().replace(",", ".");
                    Double prcVenda = prcVendaStr.isEmpty() ? null : Double.parseDouble(prcVendaStr);
                    String grupo = campos.length > 4 ? campos[4].trim() : null;
                    String referencia = campos.length > 6 ? campos[6].trim() : null;
                    String qtdAtualStr = campos.length > 7 ? campos[7].trim().replace(",", ".") : null;
                    Integer qtdAtual = (qtdAtualStr == null || qtdAtualStr.isEmpty()) ? null : (int) Double.parseDouble(qtdAtualStr);

                    produtos.add(Produto.builder()
                            .id(id)
                            .descricao(descricao)
                            .precoVenda(prcVenda)
                            .grupo(grupo)
                            .referencia(referencia)
                            .qtdAtual(qtdAtual)
                            .build());

                } catch (Exception e) {
                    erros++;
                    log.warn("Erro ao importar linha: {} — {}", linha, e.getMessage());
                }
            }

            log.info("Salvando {} produtos no banco...", produtos.size());
            produtoRepository.saveAll(produtos);

        } catch (Exception e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage());
        }

        log.info("Importação concluída. Importados: {}, Erros: {}", produtos.size(), erros);
    }
}
package com.example.webIA.Service;

import com.example.webIA.Model.Cliente;
import com.example.webIA.Repository.ClienteRepository;
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
public class ClienteImportService {

    private final ClienteRepository clienteRepository;

    public void importar(String caminhoArquivo) {
        log.info("Iniciando importação de clientes: {}", caminhoArquivo);
        List<Cliente> clientes = new ArrayList<>();
        int erros = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                String[] campos = linha.split(";");
                if (campos.length < 14) continue;

                try {
                    Long id = Long.parseLong(campos[0].trim());
                    String nome = campos[2].trim();
                    String cidade = campos.length > 10 ? campos[10].trim() : null;
                    String estado = campos.length > 11 ? campos[11].trim() : null;
                    String dtUltCompra = campos.length > 13 ? campos[13].trim() : null;
                    String contato = campos.length > 14 ? campos[14].trim() : null;
                    String dddCelular = campos.length > 19 ? campos[19].trim() : null;
                    String foneCelular = campos.length > 20 ? campos[20].trim() : null;
                    String emailContato = campos.length > 23 ? campos[23].trim() : null;
                    String cnpj = campos.length > 28 ? campos[28].trim() : null;

                    clientes.add(Cliente.builder()
                            .id(id)
                            .nome(nome)
                            .nomeContato(contato)
                            .cnpj(cnpj)
                            .dddCelular(dddCelular)
                            .foneCelular(foneCelular)
                            .emailContato(emailContato)
                            .cidade(cidade)
                            .estado(estado)
                            .dtUltCompra(dtUltCompra)
                            .build());

                } catch (Exception e) {
                    erros++;
                    log.warn("Erro ao importar cliente: {} — {}", linha, e.getMessage());
                }
            }

            log.info("Salvando {} clientes no banco...", clientes.size());
            clienteRepository.saveAll(clientes);

        } catch (Exception e) {
            log.error("Erro ao ler arquivo: {}", e.getMessage());
        }

        log.info("Importação concluída. Importados: {}, Erros: {}", clientes.size(), erros);
    }
}
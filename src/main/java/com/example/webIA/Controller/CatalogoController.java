package com.example.webIA.Controller;

import com.example.webIA.Service.CatalogoImportProdutosService;
import com.example.webIA.Service.ClienteImportService;
import com.example.webIA.Service.ProdutoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
public class CatalogoController {

    private final CatalogoImportProdutosService catalogoImportProdutosService;
    private final ProdutoService produtoService;

    @Autowired
    private final ClienteImportService clienteImportService;

    @PostMapping("/importar-catalogo")
    public ResponseEntity<String> importar(@RequestParam String caminho) {
        catalogoImportProdutosService.importar(caminho);
        return ResponseEntity.ok("Importação iniciada");
    }

    @PostMapping("/importar-clientes")
    public ResponseEntity<String> importarClientes(@RequestParam String caminho) {
        clienteImportService.importar(caminho);
        return ResponseEntity.ok("Importação de clientes iniciada");
    }

    @GetMapping("/testar-busca")
    public ResponseEntity<String> testarBusca(@RequestParam String termo) {
        String resultado = produtoService.buscarProdutos(termo);
        if (resultado == null) {
            return ResponseEntity.ok("Nenhum produto encontrado para: " + termo);
        }
        return ResponseEntity.ok(resultado);
    }

}

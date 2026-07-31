package com.example.webIA.Controller;

import com.example.webIA.Model.Atendimento;
import com.example.webIA.Model.Conversa;
import com.example.webIA.Repository.AtendimentoRepository;
import com.example.webIA.Repository.ConversaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimentos")
@Slf4j
@RequiredArgsConstructor
public class ConversaController {

    private final ConversaRepository conversaRepository;
    private final AtendimentoRepository atendimentoRepository;

    @GetMapping("/{id}/mensagens")
    public ResponseEntity<List<Conversa>> listarMensagens(@PathVariable UUID id) {
        return atendimentoRepository.findById(id)
                .map(a -> ResponseEntity.ok(
                        conversaRepository.findByPhoneOrderByDataAsc(a.getPhone())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atendimento> buscarAtendimento(@PathVariable UUID id) {
        return atendimentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
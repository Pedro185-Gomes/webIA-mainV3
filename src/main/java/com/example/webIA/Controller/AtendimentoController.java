package com.example.webIA.Controller;

import com.example.webIA.Model.Atendimento;
import com.example.webIA.Model.StatusAtendimento;
import com.example.webIA.Repository.AtendimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimentos")
@Slf4j
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoRepository atendimentoRepository;

    @GetMapping
    public ResponseEntity<List<Atendimento>> listarTodos() {
        return ResponseEntity.ok(atendimentoRepository.findAllByOrderByAtualizadoEmDesc());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Atendimento>> listarPorStatus(@PathVariable StatusAtendimento status) {
        return ResponseEntity.ok(atendimentoRepository.findByStatusOrderByAtualizadoEmDesc(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Atendimento> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam StatusAtendimento status) {

        return atendimentoRepository.findById(id)
                .map(a -> {
                    a.setStatus(status);
                    a.setAtualizadoEm(LocalDateTime.now());
                    return ResponseEntity.ok(atendimentoRepository.save(a));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

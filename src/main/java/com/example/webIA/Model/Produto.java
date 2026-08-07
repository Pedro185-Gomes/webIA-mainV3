package com.example.webIA.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column
    private Double precoVenda;

    @Column
    private String grupo;

    @Column
    private String referencia;

    @Column
    private Integer qtdAtual;
}

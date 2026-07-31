package com.example.webIA.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    private Long id;

    @Column
    private String nome;

    @Column
    private String nomeContato;

    @Column
    private String cnpj;

    @Column
    private String dddCelular;

    @Column
    private String foneCelular;

    @Column
    private String emailContato;

    @Column
    private String dtUltCompra;

    @Column
    private String cidade;

    @Column
    private String estado;
}

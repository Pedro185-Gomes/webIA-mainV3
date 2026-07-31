package com.example.webIA.Repository;

import com.example.webIA.Model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {


    List<Produto> findByDescricaoContainingIgnoreCaseOrReferenciaContainingIgnoreCase(String descricao, String referencia);

    List<Produto> findByGrupoIgnoreCase(String grupo);

    @Query("SELECT p FROM Produto p WHERE (LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(p.referencia) LIKE LOWER(CONCAT('%', :termo, '%'))) AND p.qtdAtual > 0")
    List<Produto> buscarPorTermoComEstoque(@Param("termo") String termo);

}

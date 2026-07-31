package com.example.webIA.Repository;

import com.example.webIA.Model.Atendimento;
import com.example.webIA.Model.StatusAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {

    Optional<Atendimento> findByPhone(String phone);

    List<Atendimento> findByStatusOrderByAtualizadoEmDesc(StatusAtendimento status);

    List<Atendimento> findAllByOrderByAtualizadoEmDesc();

}

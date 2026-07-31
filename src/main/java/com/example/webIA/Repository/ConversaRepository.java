package com.example.webIA.Repository;

import com.example.webIA.Model.Conversa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversaRepository extends JpaRepository<Conversa, UUID> {

    List<Conversa> findByPhoneOrderByDataAsc(String phone);

}

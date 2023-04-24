package com.example.MegaUp_Server.repositories;

import com.example.MegaUp_Server.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByNome(String nome);

}

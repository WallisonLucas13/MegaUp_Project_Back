package com.example.MegaUp_Server.repositories;

import com.example.MegaUp_Server.models.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtapaRepository extends JpaRepository<Etapa, Long> {
}

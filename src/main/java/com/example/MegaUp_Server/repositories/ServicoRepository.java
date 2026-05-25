package com.example.MegaUp_Server.repositories;

import com.example.MegaUp_Server.models.Servico;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @Query("SELECT s FROM Servico s JOIN s.etapas e WHERE e.id = :etapaId")
    Optional<Servico> findByEtapaId(@Param("etapaId") Long etapaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Servico s WHERE s.id = :id")
    Optional<Servico> findByIdForUpdate(@Param("id") Long id);
}
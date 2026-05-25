package com.example.MegaUp_Server.security.repository;

import com.example.MegaUp_Server.security.enums.RoleName;
import com.example.MegaUp_Server.security.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    Optional<UserModel> findByUsername(String username);
    boolean existsByUsername(String username);

    // I3: filtra no banco para evitar carregar todos os usuários em memória
    @Query("SELECT u FROM UserModel u WHERE NOT EXISTS " +
           "(SELECT r FROM u.roles r WHERE r.roleName = :roleName)")
    List<UserModel> findAllExcludingRole(@Param("roleName") RoleName roleName);
}

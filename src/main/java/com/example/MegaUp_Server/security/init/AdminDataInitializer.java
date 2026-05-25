package com.example.MegaUp_Server.security.init;

import com.example.MegaUp_Server.security.enums.RoleName;
import com.example.MegaUp_Server.security.model.RoleModel;
import com.example.MegaUp_Server.security.model.UserModel;
import com.example.MegaUp_Server.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class AdminDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Usuário ADMIN já existe. Nenhuma ação necessária.");
            return;
        }

        RoleModel adminRole = new RoleModel();
        adminRole.setRoleName(RoleName.ROLE_ADMIN);

        RoleModel userRole = new RoleModel();
        userRole.setRoleName(RoleName.ROLE_USER);

        UserModel admin = new UserModel(adminUsername, passwordEncoder.encode(adminPassword));
        admin.setRoles(List.of(adminRole, userRole));

        userRepository.save(admin);
        log.info("Usuário ADMIN criado com sucesso na inicialização.");
    }
}

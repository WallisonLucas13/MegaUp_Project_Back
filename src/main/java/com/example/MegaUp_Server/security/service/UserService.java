package com.example.MegaUp_Server.security.service;

import com.example.MegaUp_Server.exceptions.EntidadeJaExisteException;
import com.example.MegaUp_Server.security.dto.UserDto;
import com.example.MegaUp_Server.security.enums.RoleName;
import com.example.MegaUp_Server.security.model.AuthenticationResponse;
import com.example.MegaUp_Server.security.model.RoleModel;
import com.example.MegaUp_Server.security.model.UserModel;
import com.example.MegaUp_Server.security.model.UserViewModel;
import com.example.MegaUp_Server.security.repository.UserRepository;
import com.example.MegaUp_Server.services.SendMailService;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;
    private final Cache<String, String> adminCodeCache;

    @Value("${ADMIN_KEY}")
    private String ADMIN_KEY;

    // S2: codifica ADMIN_KEY (plain-text da env var) em BCrypt uma vez no startup.
    // Isso garante que matches() funcione corretamente sem exigir que a env var já seja um hash.
    @PostConstruct
    private void encodeAdminKey() {
        if (!ADMIN_KEY.startsWith("$2")) {
            this.ADMIN_KEY = passwordEncoder.encode(ADMIN_KEY);
        }
    }

    @Transactional
    public void register(UserDto dto) {
        log.info("Registrando usuário [username={}]", dto.toUser().getUsername());
        UserModel userModel = dto.toUser();

        boolean exist = repository.existsByUsername(userModel.getUsername());
        if (exist) {
            throw new EntidadeJaExisteException("Username já cadastrado!");
        }

        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));

        if (dto.getWritePermission() == null) {
            dto.setWritePermission("false");
        }

        if (dto.getWritePermission().equals("true")) {
            userModel.setRoles(List.of(generateRoleUser()));
        } else {
            userModel.setRoles(List.of(generateRoleRead()));
        }

        repository.save(userModel);
        log.info("Usuário registrado com sucesso [username={}, permissão={}]",
                userModel.getUsername(), dto.getWritePermission());
    }

    @Transactional
    public AuthenticationResponse login(UserDto modelDto) {
        log.info("Tentativa de login [username={}]", modelDto.toUser().getUsername());
        UserModel model = modelDto.toUser();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        model.getUsername(),
                        model.getPassword()
                )
        );

        UserModel userModel = repository.findByUsername(model.getUsername())
                .orElseThrow(IllegalArgumentException::new);

        List<String> roles = userModel.getRoles().stream()
                .map(r -> r.getRoleName().name()).toList();

        if (roles.contains(RoleName.ROLE_ADMIN.name())) {

            // C2: verificar null antes de comparar para evitar NullPointerException
            if (modelDto.getChaveAccess() == null) {
                log.warn("Login admin rejeitado: chave de acesso ausente [username={}]", userModel.getUsername());
                throw new IllegalArgumentException();
            }

            // S2: compara com o hash BCrypt gerado em memória no @PostConstruct
            if (!passwordEncoder.matches(modelDto.getChaveAccess(), ADMIN_KEY)) {
                log.warn("Login admin rejeitado: chave de acesso inválida [username={}]", userModel.getUsername());
                throw new IllegalArgumentException();
            }

            try {
                String code = this.sendMailService.createMailAndSend();
                // Armazena code → username. Token só será emitido após confirmação do código 2FA.
                adminCodeCache.put(code, userModel.getUsername());
                log.info("2FA solicitado com sucesso [username={}]", userModel.getUsername());
            } catch (Exception e) {
                log.error("Falha ao enviar e-mail de acesso 2FA [username={}]: {}", userModel.getUsername(), e.getMessage());
                throw new RuntimeException("Falha ao enviar código de acesso. Tente novamente.");
            }

            return AuthenticationResponse
                    .builder()
                    .pendingTwoFactor(true)
                    .build();
        }

        log.info("Login realizado com sucesso [username={}]", userModel.getUsername());
        return AuthenticationResponse
                .builder()
                .token(jwtService.generateToken(userModel))
                .build();
    }

    // I6: loginWithToken removido — validar token é responsabilidade do JwtAuthenticationFilter.
    // O endpoint /api/login/access foi removido pois duplicava a lógica do filtro.

    @Transactional
    public boolean deleteByUsername(String username) throws UsernameNotFoundException{
        log.info("Removendo usuário [username={}]", username);
            UserModel userModel = repository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(""));

            if (filterUsersByRoles(userModel.getRoles())) {
                repository.deleteById(userModel.getId());
                log.info("Usuário removido com sucesso [username={}]", username);
                return true;
            }
            log.warn("Remoção bloqueada: usuário é administrador [username={}]", username);
            return false;
    }

    @Transactional
    public String codeKeyAccessVerify(String code) throws IllegalAccessException {
        log.info("Verificando código 2FA");
        String username = adminCodeCache.getIfPresent(code);
        if (username == null) {
            log.warn("Código 2FA inválido ou expirado");
            throw new IllegalAccessException("Código inválido ou expirado.");
        }

        adminCodeCache.invalidate(code);

        UserModel user = repository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuário associado ao código não encontrado."));
        log.info("2FA validado com sucesso, token emitido [username={}]", username);
        return jwtService.generateToken(user);
    }

    @Transactional
    public List<UserViewModel> findAllUsers(){
        // I3: filtro feito no banco — evita carregar todos os usuários em memória
        return repository.findAllExcludingRole(RoleName.ROLE_ADMIN)
                .stream()
                .map(user -> new UserViewModel(user.getUsername(), viewRoles(user.getRoles())))
                .collect(Collectors.toList());
    }

    private String viewRoles(List<RoleModel> roles){

        List<RoleModel> list = roles.stream()
                .filter(role -> role.getRoleName() == RoleName.ROLE_READ).toList();

        if(list.isEmpty()){
            return "Leitura/Escrita";
        }

        return "Leitura";
    }

    private boolean filterUsersByRoles(List<RoleModel> roles){
        return roles.stream()
                .filter(role -> role.getRoleName() == RoleName.ROLE_ADMIN)
                .collect(Collectors.toList())
                .isEmpty();
    }

    private RoleModel generateRoleUser(){
        RoleModel roleModel = new RoleModel();
        roleModel.setRoleName(RoleName.ROLE_USER);
        return roleModel;
    }

    private RoleModel generateRoleRead(){
        RoleModel roleModel = new RoleModel();
        roleModel.setRoleName(RoleName.ROLE_READ);
        return roleModel;
    }

}

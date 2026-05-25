package com.example.MegaUp_Server.security.controller;

import com.example.MegaUp_Server.security.dto.UserDto;
import com.example.MegaUp_Server.security.model.AuthenticationResponse;
import com.example.MegaUp_Server.security.model.CodeKeyModel;
import com.example.MegaUp_Server.security.model.UserViewModel;
import com.example.MegaUp_Server.security.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid UserDto dto) {
        log.info("POST /api/login - tentativa de login [username={}]", dto.getUsername());
        AuthenticationResponse response = service.login(dto);
        if (Boolean.TRUE.equals(response.isPendingTwoFactor())) {
            log.info("POST /api/login - 2FA solicitado [username={}]", dto.getUsername());
        } else {
            log.info("POST /api/login - login realizado com sucesso [username={}]", dto.getUsername());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserDto dto) {
        log.info("POST /api/register - criando usuário [username={}]", dto.getUsername());
        service.register(dto);
        log.info("POST /api/register - usuário criado com sucesso [username={}]", dto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserViewModel>> users() {
        log.info("GET /api/users - listando usuários");
        List<UserViewModel> result = service.findAllUsers();
        log.info("GET /api/users - {} usuário(s) retornado(s)", result.size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> delete(@PathVariable String username) {
        log.info("DELETE /api/users/{} - removendo usuário", username);
        if (service.deleteByUsername(username)) {
            log.info("DELETE /api/users/{} - usuário removido com sucesso", username);
            return ResponseEntity.ok().build();
        }
        log.warn("DELETE /api/users/{} - remoção bloqueada (usuário é administrador)", username);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não é possível excluir administradores.");
    }

    @PostMapping("/user/access")
    public ResponseEntity<AuthenticationResponse> verify(@RequestBody @Valid CodeKeyModel codeKeyModel) throws IllegalAccessException {
        log.info("POST /api/user/access - verificando código 2FA");
        String token = service.codeKeyAccessVerify(codeKeyModel.getCode());
        log.info("POST /api/user/access - 2FA validado, token emitido");
        return ResponseEntity.ok(AuthenticationResponse.builder().token(token).build());
    }
}

package com.example.MegaUp_Server.security.service;

import com.example.MegaUp_Server.security.model.UserModel;
import com.example.MegaUp_Server.security.repository.UserRepository;
import com.example.MegaUp_Server.services.SendMailService;
import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * T1: testes unitários do fluxo 2FA — codeKeyAccessVerify.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SendMailService sendMailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Cache<String, String> adminCodeCache;

    @InjectMocks
    private UserService service;

    // -------------------------------------------------------
    // T1: codeKeyAccessVerify — fluxo 2FA do ADMIN
    // -------------------------------------------------------

    @Test
    void codeKeyAccessVerifyComCodigoValidoEmiteTokenEInvalidaCache() throws IllegalAccessException {
        UserModel admin = new UserModel();

        when(adminCodeCache.getIfPresent("ABC123")).thenReturn("admin");
        when(repository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(jwtService.generateToken(admin)).thenReturn("jwt-token");

        String token = service.codeKeyAccessVerify("ABC123");

        assertThat(token).isEqualTo("jwt-token");
        // código deve ser invalidado do cache após uso (single-use)
        verify(adminCodeCache).invalidate("ABC123");
    }

    @Test
    void codeKeyAccessVerifyComCodigoInvalidoLancaIllegalAccessException() {
        when(adminCodeCache.getIfPresent("ERRADO")).thenReturn(null);

        assertThatThrownBy(() -> service.codeKeyAccessVerify("ERRADO"))
                .isInstanceOf(IllegalAccessException.class)
                .hasMessageContaining("inválido ou expirado");
    }

    @Test
    void codeKeyAccessVerifyComCodigoExpiradoLancaIllegalAccessException() {
        // Cache retorna null tanto para código expirado quanto inválido
        when(adminCodeCache.getIfPresent("EXPIRADO")).thenReturn(null);

        assertThatThrownBy(() -> service.codeKeyAccessVerify("EXPIRADO"))
                .isInstanceOf(IllegalAccessException.class);
    }
}

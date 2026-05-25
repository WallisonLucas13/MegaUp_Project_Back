package com.example.MegaUp_Server.security.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationResponse {
    private String token;
    /** true quando o login de ADMIN aguarda confirmação do código 2FA. */
    private boolean pendingTwoFactor;
}

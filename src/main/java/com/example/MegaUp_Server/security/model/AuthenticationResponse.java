package com.example.MegaUp_Server.security.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticationResponse {
    private String token;
}

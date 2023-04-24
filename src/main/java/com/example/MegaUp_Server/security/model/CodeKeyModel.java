package com.example.MegaUp_Server.security.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeKeyModel {

    @NotBlank
    private String code;
}

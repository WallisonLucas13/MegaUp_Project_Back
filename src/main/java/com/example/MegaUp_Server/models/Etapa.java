package com.example.MegaUp_Server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long iden;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal valor;
}

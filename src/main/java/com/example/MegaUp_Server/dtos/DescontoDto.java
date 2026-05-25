package com.example.MegaUp_Server.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DescontoDto(@Min(0) @Max(100) int porcentagem) {}

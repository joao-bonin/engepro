package br.com.engepro.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class FunnelDTO {

    @NotBlank(message = "Nome do funnel é obrigatório")
    String name

    @Size(max = 140, message = "Descrição deve ter no máximo 140 caracteres")
    @NotNull(message = "Descrição é obrigatória")
    String description
}

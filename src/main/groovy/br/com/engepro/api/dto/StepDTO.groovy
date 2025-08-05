package br.com.engepro.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class StepDTO {

    @NotBlank(message = "Name is required")
    String name

    @Size(max = 140, message = "Description must be less than 140 characters")
    String description

    @NotBlank(message = "Funnel is required")
    Long funnelId
}

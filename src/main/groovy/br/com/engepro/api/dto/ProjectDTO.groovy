package br.com.engepro.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.time.LocalDateTime

class ProjectDTO {

    @NotBlank(message = "O nome do projeto é obrigatório")
    String name

    @Size(max = 1500, message = "Descrição deve ter no máximo 1500 caracteres")
    @NotNull(message = "Descrição é obrigatória")
    String description

    @NotNull(message = "O ID do Usuário é obrigatório")
    Long userId

    @NotNull(message = "O ID do Passo é obrigatório")
    Long stepId

    @NotNull(message = "O ID do Contato é obrigatório")
    Long contactId

    @NotNull(message = "Data/hora é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate

    @NotNull(message = "Data/hora é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate

    @NotNull(message = "O projeto deve ser arquivado ou não")
    Boolean isArchived = false
}

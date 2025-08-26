package br.com.engepro.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class UserDTO {

    @NotNull(message = "Preencha o nome")
    String name

    @Email(message = "Email inválido")
    String email

    @Size(min = 8, message = "Senha inválida")
    @NotNull(message = "Preencha a senha")
    String password

    @NotNull(message = "Preencha o nível de acesso")
    Boolean hasLevelConfig
}

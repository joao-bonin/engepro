package br.com.engepro.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

class LoginDTO {

    @Email
    @NotNull
    String email

    @NotNull
    String password
}

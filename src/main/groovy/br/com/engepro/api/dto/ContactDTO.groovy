package br.com.engepro.api.dto


import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.br.CNPJ

class ContactDTO {

    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name

    @NotNull(message = "Email não pode ser nulo")
    @Email(message = "Email precisa ser válido")
    String email

    String phone

    AddressDTO address

    @Size(max = 1500, message = "Descrição deve ter no máximo 1500 caracteres")
    String observations

    @CNPJ(message = "CNPJ precisa ser válido")
    String cnpj
}

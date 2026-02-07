package br.com.engepro.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Contact extends BaseEntity {

    String name

    String email

    String phone

    String cnpj

    String observations

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    Address address

    @Override
    String toString() {
        return "Contact(name='$name', email='$email', phone='$phone')"
    }
}

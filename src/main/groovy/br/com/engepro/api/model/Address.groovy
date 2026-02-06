package br.com.engepro.api.model

import jakarta.persistence.Entity

@Entity
class Address extends BaseEntity {

    String street

    String number

    String city

    String state

    String quarter

    String zipCode

    @Override
    String toString() {
        return "Address(street='$street', number='$number', city='$city', state='$state', quarter='$quarter', zipCode='$zipCode')"
    }
}

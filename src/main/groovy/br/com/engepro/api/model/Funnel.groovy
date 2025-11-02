package br.com.engepro.api.model


import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

@Entity
class Funnel extends BaseEntity {

    String name
    String description

    @OneToMany(mappedBy = "funnel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Step> steps = []

    @Override
    String toString() {
        return "Funnel(id=$id, name=$name)"
    }
}

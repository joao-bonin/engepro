package br.com.engepro.api.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

@Entity
class Funnel extends BaseEntity {

    String name

    String description

    @OneToMany(mappedBy = "funnel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<Step> steps = []

    @Override
    String toString() {
        return "Funnel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", steps=" + steps +
                '}';
    }
}


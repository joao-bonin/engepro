package br.com.engepro.api.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class Step extends BaseEntity {

    String name

    String description

    @ManyToOne
    Funnel funnel

    @Override
    String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", funnel=" + funnel +
                '}';
    }
}

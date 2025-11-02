package br.com.engepro.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
class Step extends BaseEntity {

    String name
    String description

    @ManyToOne
    @JoinColumn(name = "funnel_id", nullable = false)
    @JsonIgnore
    Funnel funnel

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Project> projects = []

    @Override
    String toString() {
        return "Step(name='$name', description='$description')"
    }
}

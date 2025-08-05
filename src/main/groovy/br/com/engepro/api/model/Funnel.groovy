package br.com.engepro.api.model

import groovy.transform.ToString
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany

@Entity
@ToString(includeNames = true)
class Funnel extends BaseEntity {

    String name

    String description

    @OneToMany(mappedBy = "funnel")
    List<Step> steps = []
}


package br.com.engepro.api.model

import groovy.transform.ToString
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne


@Entity
@ToString(includeNames = true)
class Step extends BaseEntity {

    String name

    String description

    @ManyToOne
    Funnel funnel
}

package br.com.engepro.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

import java.time.LocalDateTime

@Entity
class Project extends BaseEntity {

    String name
    String description
    String customer
    Boolean isArchived

    LocalDateTime startDate
    LocalDateTime endDate

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user

    @ManyToOne
    @JoinColumn(name = "step_id", nullable = false)
    @JsonIgnore
    Step step

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    @JsonIgnore
    Contact contact

    @Override
    String toString() {
        return "Project(name='$name', description='$description')"
    }
}

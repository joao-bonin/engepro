package br.com.engepro.api.model

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id
    
    LocalDateTime lastUpdated
    
    LocalDateTime dateCreated
    
    @PreUpdate
    protected void preUpdate() {
        this.lastUpdated = LocalDateTime.now()
    }
    
    @PrePersist
    protected void prePersist() {
        this.dateCreated = LocalDateTime.now()
        this.lastUpdated = LocalDateTime.now()
    }
}

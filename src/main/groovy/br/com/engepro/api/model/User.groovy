package br.com.engepro.api.model

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Transient
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.time.LocalDateTime

@Entity
class User extends BaseEntity implements UserDetails {

    String name

    String email

    String password

    Boolean active = true

    Boolean hasLevelConfig = false

    LocalDateTime lastLogin

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_funnel_access",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "funnel_id"))
    Set<Funnel> allowedFunnels = [] as Set


    @Override
    String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", hasLevelConfig=" + hasLevelConfig +
                ", lastLogin=" + lastLogin +
                '}'
    }

    @Override
    @Transient
    Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of()
    }

    @Override
    String getUsername() {
        return email
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return active
    }
}

package br.com.engepro.api.repository

import br.com.engepro.api.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email)
}

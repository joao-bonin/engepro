package br.com.engepro.api.controller

import br.com.engepro.api.dto.UserDTO
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.UserRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

import java.time.LocalDateTime

@Slf4j
@RestController
@RequestMapping(path = "/user")
class UserController {

    UserRepository userRepository

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @GetMapping
    ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll())
    }

    @GetMapping(path = "/active")
    ResponseEntity getActiveUsers() {
        return ResponseEntity.ok().body(userRepository.findAllByActiveIsTrue())
    }

    @PostMapping
    ResponseEntity createUser(@RequestBody @Valid UserDTO requestBody,
                              final BindingResult result, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("User is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        userRepository
                .findByEmail(requestBody.email)
                .ifPresent { it -> return ResponseEntity.badRequest().body("User already exists") }

        User userToCreate = new User(name: requestBody.name,
                email: requestBody.email,
                password: encode(requestBody.password),
                active: true,
                hasLevelConfig: requestBody.hasLevelConfig,
                lastLogin: LocalDateTime.now())

        userRepository.save(userToCreate)
        log.info("User created: {}", userToCreate)

        return ResponseEntity.ok().body(userToCreate)
    }

    static String encode(CharSequence rawPassword) {
        new BCryptPasswordEncoder().encode(rawPassword)
    }
}

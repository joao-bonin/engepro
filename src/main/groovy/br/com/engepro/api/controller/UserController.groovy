package br.com.engepro.api.controller

import br.com.engepro.api.dto.UserDTO
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.UserRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime

@Slf4j
@RestController
@RequestMapping(path = "/user")
class UserController {

    UserRepository userRepository

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @PostMapping
    ResponseEntity createUser(@RequestBody @Valid UserDTO requestBody,
                              final BindingResult result) {

        if (result.hasErrors()) {
            log.warn("User is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        userRepository
                .findByEmail(requestBody.email)
                .ifPresent { it -> return ResponseEntity.badRequest().body("User already exists") }

        User user = new User(name: requestBody.name,
                email: requestBody.email,
                password: encode(requestBody.password),
                active: true,
                hasLevelConfig: requestBody.hasLevelConfig,
                lastLogin: LocalDateTime.now())

        userRepository.save(user)
        log.info("User created: {}", user)

        return ResponseEntity.ok().body(user)
    }

    static String encode(CharSequence rawPassword) {
        new BCryptPasswordEncoder().encode(rawPassword)
    }
}

package br.com.engepro.api.controller

import br.com.engepro.api.dto.LoginDTO
import br.com.engepro.api.dto.RegisterUserDTO
import br.com.engepro.api.infra.security.AuthenticationService
import br.com.engepro.api.infra.security.JwtService
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.UserRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime

@Slf4j
@RestController
class LoginController {

    JwtService jwtService

    UserRepository userRepository

    AuthenticationService authenticationService

    LoginController(JwtService jwtService, UserRepository userRepository, AuthenticationService authenticationService) {
        this.jwtService = jwtService
        this.userRepository = userRepository
        this.authenticationService = authenticationService
    }


    @PostMapping("/login")
    ResponseEntity login(@RequestBody @Valid LoginDTO loginDTO,
                         final BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        User user = authenticationService.authenticate(loginDTO)
        String token = jwtService.generateToken(user)

        user.setLastLogin(LocalDateTime.now())
        userRepository.save(user)

        return ResponseEntity.ok([token: token])
    }

    @PostMapping("/signup")
    ResponseEntity signup(@RequestBody RegisterUserDTO registerUserDTO) {
        User registeredUser = authenticationService.signup(registerUserDTO)

        return ResponseEntity.ok(registeredUser)
    }

    private static boolean matches(CharSequence rawPassword, String encodedPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        encoder.matches(rawPassword, encodedPassword)
    }
}

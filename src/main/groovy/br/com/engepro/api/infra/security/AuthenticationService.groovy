package br.com.engepro.api.infra.security

import br.com.engepro.api.dto.LoginDTO
import br.com.engepro.api.dto.RegisterUserDTO
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService {

    final UserRepository userRepository
    final AuthenticationManager authenticationManager

    AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository
        this.authenticationManager = authenticationManager
    }

    User signup(RegisterUserDTO input) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()
        User user = new User(
                name: input.fullName,
                email: input.email,
                password: encoder.encode(input.password),
                hasLevelConfig: true
        )
        return userRepository.save(user)
    }

    User authenticate(LoginDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.email, input.password)
        )
        return userRepository.findByEmail(input.email)
                .orElseThrow { new RuntimeException("Usuário não encontrado") }
    }
}

package br.com.engepro.api.infra.security

import br.com.engepro.api.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService implements UserDetailsService {

    final UserRepository userRepository

    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow { new UsernameNotFoundException("Usuário não encontrado: $email") }
    }
}

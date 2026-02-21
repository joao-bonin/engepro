package br.com.engepro.api.controller

import br.com.engepro.api.dto.UserDTO
import br.com.engepro.api.model.Funnel
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.FunnelRepository
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

    FunnelRepository funnelRepository

    UserController(UserRepository userRepository, FunnelRepository funnelRepository) {
        this.userRepository = userRepository
        this.funnelRepository = funnelRepository
    }

    @GetMapping
    ResponseEntity getAllUsers() {
        return ResponseEntity.ok().body(userRepository.findAll())
    }

    @GetMapping(path = "/active")
    ResponseEntity getActiveUsers() {
        return ResponseEntity.ok().body(userRepository.findAllByActiveIsTrue())
    }

    @GetMapping(path = "/{id}")
    ResponseEntity getUserById(@PathVariable Long id) {
        def user = userRepository.findById(id)

        if (!user.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().body(user.get())
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

        Set<Funnel> allowedFunnels = resolveAllowedFunnels(requestBody.hasLevelConfig, requestBody.funnelIds)
        if (allowedFunnels == null) {
            return ResponseEntity.badRequest().body("Usuário comum precisa ter ao menos um funil vinculado")
        }

        User userToCreate = new User(name: requestBody.name,
                email: requestBody.email,
                password: encode(requestBody.password),
                active: true,
                hasLevelConfig: requestBody.hasLevelConfig,
                lastLogin: LocalDateTime.now(),
                allowedFunnels: allowedFunnels)

        userRepository.save(userToCreate)
        log.info("User created: {}", userToCreate)

        return ResponseEntity.ok().body(userToCreate)
    }

    @PutMapping(path = "/{id}")
    ResponseEntity updateUser(@PathVariable Long id,
                              @RequestBody Map<String, Object> requestBody,
                              @AuthenticationPrincipal User authenticatedUser) {

        if (!authenticatedUser.hasLevelConfig) {
            return ResponseEntity.unprocessableEntity().build()
        }

        def userOptional = userRepository.findById(id)

        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        User userToUpdate = userOptional.get()

        // Atualiza apenas os campos fornecidos
        if (requestBody.containsKey('name')) {
            userToUpdate.name = requestBody.name
        }

        if (requestBody.containsKey('email')) {
            // Verifica se o email já existe em outro usuário
            def existingUser = userRepository.findByEmail(requestBody.email as String)
            if (existingUser.isPresent() && existingUser.get().id != id) {
                return ResponseEntity.badRequest().body("Email already in use by another user")
            }
            userToUpdate.email = requestBody.email
        }

        if (requestBody.containsKey('password') && requestBody.password) {
            // Valida tamanho mínimo da senha
            if ((requestBody.password as String).length() < 8) {
                return ResponseEntity.badRequest().body("Password must be at least 8 characters")
            }
            userToUpdate.password = encode(requestBody.password as String)
        }

        if (requestBody.containsKey('hasLevelConfig')) {
            userToUpdate.hasLevelConfig = requestBody.hasLevelConfig as Boolean
        }

        if (requestBody.containsKey('active')) {
            userToUpdate.active = requestBody.active as Boolean
        }

        Boolean hasLevelConfig = requestBody.containsKey('hasLevelConfig') ?
                requestBody.hasLevelConfig as Boolean : userToUpdate.hasLevelConfig

        Collection<Long> requestedFunnelIds = requestBody.containsKey('funnelIds') ?
                normalizeFunnelIds(requestBody.funnelIds) :
                userToUpdate.allowedFunnels.collect { it.id }

        Set<Funnel> allowedFunnels = resolveAllowedFunnels(hasLevelConfig, requestedFunnelIds)
        if (allowedFunnels == null) {
            return ResponseEntity.badRequest().body("Usuário comum precisa ter ao menos um funil vinculado")
        }
        userToUpdate.allowedFunnels = allowedFunnels

        userRepository.save(userToUpdate)
        log.info("User updated: {}", userToUpdate)

        return ResponseEntity.ok().body(userToUpdate)
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity deleteUser(@PathVariable Long id, @AuthenticationPrincipal User authenticatedUser) {

        if (!authenticatedUser.hasLevelConfig) {
            return ResponseEntity.unprocessableEntity().build()
        }

        if (authenticatedUser.id == id) {
            return ResponseEntity.badRequest().body("Cannot delete your own account")
        }

        Optional<User> userOptional = userRepository.findById(id)

        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        userRepository.deleteById(id)
        log.info("User deleted: {}", id)

        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}/activate")
    ResponseEntity toggleActivate(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))

        user.active = !user.isEnabled()
        userRepository.save(user)

        String action = user.isEnabled() ? "ativado" : "inativado"

        log.info("Usuário {}: {}", action, user)

        return ResponseEntity.ok([message: "Projeto ${action} com sucesso", archived: user.isEnabled()])
    }

    private Set<Funnel> resolveAllowedFunnels(Boolean hasLevelConfig, Collection<Long> funnelIds) {
        if (hasLevelConfig) {
            return [] as Set
        }

        List<Long> ids = normalizeFunnelIds(funnelIds)

        if (!ids) {
            return null
        }

        List<Funnel> funnels = funnelRepository.findAllByIdIn(ids)
        if (funnels.size() != ids.unique().size()) {
            return null
        }

        return funnels as Set
    }

    private List<Long> normalizeFunnelIds(def funnelIds) {
        if (!(funnelIds instanceof Collection)) {
            return []
        }

        return (funnelIds as Collection)
                .findAll { it != null }
                .collect { (it as Number).longValue() }
    }

    static String encode(CharSequence rawPassword) {
        new BCryptPasswordEncoder().encode(rawPassword)
    }
}
package br.com.engepro.api.controller

import br.com.engepro.api.dto.FunnelDTO
import br.com.engepro.api.model.Funnel
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.FunnelRepository
import br.com.engepro.api.repository.StepRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping(path = "/funnel")
class FunnelController {

    @Autowired
    FunnelRepository funnelRepository

    @Autowired
    StepRepository stepRepository


    @GetMapping
    ResponseEntity getAllFunnel() {
        Iterable<Funnel> funnels = funnelRepository.findAll()

        log.info("Found {} funnels", funnels.size())
        return ResponseEntity.ok().body(funnels)
    }

    @GetMapping(path = "/{id}")
    ResponseEntity getFunnelById(@PathVariable Long id) {

        Optional<Funnel> funnel = funnelRepository.findById(id)

        if (funnel.isPresent()) {
            log.info("Funnel found: {}", funnel.get())
            return ResponseEntity.ok().body(funnel.get())
        }

        log.info("Funnel not found: {}", id)
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity deleteFunnel(@PathVariable Long id, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (stepRepository.existsByFunnelId(id)) {
            log.info("Funnel {} has related steps", id)
            return ResponseEntity.ok().body(false)
        }

        funnelRepository.deleteById(id)

        log.info("Funnel deleted: {}", id)

        return ResponseEntity.ok().body(true)
    }

    @PostMapping
    ResponseEntity createFunnel(@RequestBody @Valid FunnelDTO requestBody,
                                final BindingResult result, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("Funnel is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.defaultMessage })
        }

        Funnel funnel = new Funnel(name: requestBody.name, description: requestBody.description)

        funnelRepository.save(funnel)
        log.info("Funnel created: {}", funnel)

        return ResponseEntity.ok().body(funnel)
    }

    @PutMapping(path = "/{id}")
    ResponseEntity updateFunnel(@PathVariable Long id,
                                @RequestBody @Valid FunnelDTO requestBody, final BindingResult result,
                                @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("Funnel is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.defaultMessage })
        }

        Optional<Funnel> funnel = funnelRepository.findById(id)

        if (funnel.isPresent()) {
            Funnel funnelToEdit = funnel.get()
            funnelToEdit.name = requestBody.name
            funnelToEdit.description = requestBody.description
            funnelRepository.save(funnelToEdit)

            log.info("Funnel edited: {}", funnelToEdit)
            return ResponseEntity.ok().body(funnelToEdit)
        }

        log.info("Funnel not found: {}", id)
        return ResponseEntity.notFound().build()
    }
}

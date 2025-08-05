package br.com.engepro.api.controller

import br.com.engepro.api.dto.FunnelDTO
import br.com.engepro.api.model.Funnel
import br.com.engepro.api.repository.FunnelRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
@RequestMapping(path = "/funnel")
class FunnelController {

    @Autowired
    FunnelRepository funnelRepository


    @GetMapping
    ResponseEntity getAllFunnel() {
        Iterable<Funnel> funnels = funnelRepository.findAll()

        log.info("Funnel list: {}", funnels)
        return ResponseEntity.ok().body(funnels)
    }

    @GetMapping(path = "/{id}")
    ResponseEntity getFunnel(@PathVariable Long id) {

        Optional<Funnel> funnel = funnelRepository.findById(id)

        if (funnel.isPresent()) {
            log.info("Funnel found: {}", funnel.get())
            return ResponseEntity.ok().body(funnel.get())
        }

        log.info("Funnel not found: {}", id)
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity deleteFunnel(@PathVariable Long id) {

        funnelRepository.deleteById(id)

        log.info("Funnel deleted: {}", id)

        return ResponseEntity.ok().build()
    }

    @PostMapping
    ResponseEntity createFunnel(@RequestBody @Valid FunnelDTO requestBody,
                                final BindingResult result) {

        if (result.hasErrors()) {
            log.warn("Funnel is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        Funnel funnel = new Funnel(name: requestBody.name, description: requestBody.description)

        funnelRepository.save(funnel)
        log.info("Funnel created: {}", funnel)

        return ResponseEntity.ok().body(funnel)
    }

    @PutMapping(path = "/{id}")
    ResponseEntity editFunnel(@PathVariable Long id,
                              @RequestBody @Valid FunnelDTO requestBody, final BindingResult result) {

        if (result.hasErrors()) {
            log.warn("Funnel is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
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

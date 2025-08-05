package br.com.engepro.api.controller

import br.com.engepro.api.dto.StepDTO
import br.com.engepro.api.model.Funnel
import br.com.engepro.api.model.Step
import br.com.engepro.api.repository.FunnelRepository
import br.com.engepro.api.repository.StepRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping(path = "/step")
class StepController {

    @Autowired
    StepRepository stepRepository

    @Autowired
    FunnelRepository funnelRepository


    @PostMapping
    ResponseEntity createStep(@RequestBody @Valid StepDTO requestBody,
                              final BindingResult result) {

        if (result.hasErrors()) {
            log.warn("Step is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        Funnel funnel = funnelRepository
                .findById(requestBody.funnelId)
                .orElseThrow(() -> new RuntimeException("Funnel not found"))

        Step step = new Step(name: requestBody.name, description: requestBody.description)
        funnel.steps.add(step)

        funnelRepository.save(funnel)
        log.info("Step created: {}", step)

        return ResponseEntity.ok().body(step)
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity deleteStep(@PathVariable Long id) {

        stepRepository.deleteById(id)

        log.info("Step deleted: {}", id)

        return ResponseEntity.ok().build()
    }
}

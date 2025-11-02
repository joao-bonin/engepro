package br.com.engepro.api.controller


import br.com.engepro.api.dto.StepDTO
import br.com.engepro.api.model.Funnel
import br.com.engepro.api.model.Step
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.FunnelRepository
import br.com.engepro.api.repository.StepRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@Transactional
@RequestMapping(path = "/step")
class StepController {

    @Autowired
    StepRepository stepRepository

    @Autowired
    FunnelRepository funnelRepository


    @PostMapping
    ResponseEntity createStep(@RequestBody @Valid StepDTO requestBody,
                              final BindingResult result, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("Step is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        Funnel funnel = funnelRepository
                .findById(requestBody.funnelId)
                .orElseThrow(() -> new RuntimeException("Funnel not found"))

        Step step = new Step(name: requestBody.name, description: requestBody.description, funnel: funnel)
        funnel.steps.add(step)

        funnelRepository.save(funnel)

        log.info("Step created: {}", step)
        return ResponseEntity.ok().body(step)
    }

    @PutMapping(path = "/{id}")
    ResponseEntity updateStep(@PathVariable Long id,
                              @RequestBody @Valid StepDTO requestBody, final BindingResult result,
                              @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("Step is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.defaultMessage })
        }

        Optional<Step> step = stepRepository.findById(id)

        if (step.isPresent()) {
            Step stepToEdit = step.get()
            stepToEdit.name = requestBody.name
            stepToEdit.description = requestBody.description
            stepRepository.save(stepToEdit)

            log.info("Step edited: {}", stepToEdit)
            return ResponseEntity.ok().body(stepToEdit)
        }

        log.info("Step not found: {}", id)
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity deleteStep(@PathVariable Long id, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        Optional<Step> stepOpt = stepRepository.findById(id)

        if (stepOpt.isEmpty()) {
            log.info("The step ${id} was not found!")
            return ResponseEntity.notFound().build()
        }

        Step step = stepOpt.get()
        Funnel funnel = step.getFunnel()

        funnel.getSteps().remove(step)
        funnelRepository.save(funnel)

        log.info("Step removed via Funnel: {}", id)
        return ResponseEntity.noContent().build()
    }
}

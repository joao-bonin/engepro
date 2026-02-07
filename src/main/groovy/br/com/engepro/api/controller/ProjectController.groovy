package br.com.engepro.api.controller

import br.com.engepro.api.dto.ProjectDTO
import br.com.engepro.api.model.Contact
import br.com.engepro.api.model.Project
import br.com.engepro.api.model.Step
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.ContactRepository
import br.com.engepro.api.repository.ProjectRepository
import br.com.engepro.api.repository.StepRepository
import br.com.engepro.api.repository.UserRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@Transactional
@RequestMapping(path = "/project")
class ProjectController {

    @Autowired
    ProjectRepository projectRepository

    @Autowired
    StepRepository stepRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ContactRepository contactRepository


    @GetMapping
    ResponseEntity listAll() {
        List<Project> projects = projectRepository.findAll()
                .findAll { it.isArchived != true }

        List<Map> response = projects.collect {
            [
                    id         : it.id,
                    name       : it.name,
                    description: it.description,
                    contactId  : it.contact?.id,
                    startDate  : it.startDate,
                    endDate    : it.endDate,
                    userId     : it.user?.id,
                    stepId     : it.step?.id,
                    isArchived : it.isArchived == true
            ]
        }

        return ResponseEntity.ok(response)
    }


    @GetMapping("/{id}")
    ResponseEntity getById(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"))

        Map response = [
                id         : project.id,
                name       : project.name,
                description: project.description,
                contactId  : project.contact?.id,
                startDate  : project.startDate,
                endDate    : project.endDate,
                userId     : project.user?.id,
                stepId     : project.step?.id,
                isArchived : project.isArchived == true
        ]

        return ResponseEntity.ok(response)
    }

    @GetMapping(path = "/funnel/{id}")
    ResponseEntity getByFunnelId(@PathVariable Long id,
                                 @RequestParam(required = false) Boolean archived) {

        List<Step> steps = stepRepository.findByFunnelId(id)
        if (steps.isEmpty()) {
            return ResponseEntity.status(404).body([message: "Funil não encontrado ou sem etapas"])
        }

        List<Map> stepsResponse = steps.collect { Step step ->

            List<Project> filteredProjects = step.projects.findAll { project ->
                if (archived == null) {
                    return true
                }
                return project.isArchived == archived
            }

            List<Map> projectsResponse = filteredProjects.collect { Project project ->
                [
                        id         : project.id,
                        name       : project.name,
                        description: project.description,
                        contactId  : project.contact?.id,
                        startDate  : project.startDate,
                        endDate    : project.endDate,
                        isArchived : project.isArchived,
                        userId     : project.user?.id,
                        stepId     : step.id
                ]
            }

            return [
                    id      : step.id,
                    name    : step.name,
                    projects: projectsResponse
            ]
        }

        Map response = [
                funnelId: id,
                steps   : stepsResponse
        ]

        log.info("Retornando {} etapas e {} projetos (archived={}) para o funil {}",
                stepsResponse.size(),
                stepsResponse.sum { it.projects.size() },
                archived,
                id
        )

        return ResponseEntity.ok(response)
    }


    @PostMapping
    ResponseEntity create(@Valid @RequestBody ProjectDTO dto) {

        User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))

        Step step = stepRepository.findById(dto.stepId)
                .orElseThrow(() -> new RuntimeException("Etapa não encontrada"))

        Contact contact = contactRepository.findById(dto.contactId)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"))

        Project project = new Project(
                name: dto.name,
                description: dto.description,
                contact: contact,
                startDate: dto.startDate,
                endDate: dto.endDate,
                user: user,
                step: step,
                isArchived: dto.isArchived
        )

        projectRepository.save(project)
        log.info("Projeto criado: {}", project)

        return ResponseEntity.ok([message: "Projeto criado com sucesso", id: project.id])
    }


    @PutMapping("/{id}")
    ResponseEntity update(@PathVariable Long id, @Valid @RequestBody ProjectDTO dto) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"))

        User user = userRepository.findById(dto.userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))

        Step step = stepRepository.findById(dto.stepId)
                .orElseThrow(() -> new RuntimeException("Etapa não encontrada"))

        Contact contact = contactRepository.findById(dto.contactId)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"))

        project.with {
            it.name = dto.name
            it.description = dto.description
            it.contact = contact
            it.startDate = dto.startDate
            it.endDate = dto.endDate
            it.user = user
            it.step = step
            it.isArchived = dto.isArchived
        }

        projectRepository.save(project)
        log.info("Projeto atualizado: {}", project)

        return ResponseEntity.ok([message: "Projeto atualizado com sucesso"])
    }


    @PutMapping("/{id}/archive")
    ResponseEntity toggleArchive(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"))

        project.isArchived = !project.isArchived
        projectRepository.save(project)

        String action = project.isArchived ? "arquivado" : "desarquivado"

        log.info("Projeto {}: {}", action, project)

        return ResponseEntity.ok([message: "Projeto ${action} com sucesso", archived: project.isArchived])
    }
}

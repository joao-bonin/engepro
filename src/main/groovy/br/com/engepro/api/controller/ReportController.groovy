package br.com.engepro.api.controller

import br.com.engepro.api.repository.FunnelRepository
import br.com.engepro.api.repository.ProjectRepository
import br.com.engepro.api.repository.StepRepository
import br.com.engepro.api.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Slf4j
@RestController
@Transactional
@RequestMapping(path = "/report")
class ReportController {

    @Autowired
    StepRepository stepRepository

    @Autowired
    FunnelRepository funnelRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ProjectRepository projectRepository

    @GetMapping
    ResponseEntity report(@RequestParam(name = "funnelId", required = false) Integer funnelFilter) {
        def projects = projectRepository.findAll()
        def users = userRepository.findAll()
        def funnels = funnelRepository.findAll()
        def now = LocalDateTime.now()

        if (funnelFilter != null) {
            projects = projects.findAll { project ->
                project.step?.funnel?.id == funnelFilter
            }
        }

        // Mapear funis para saber qual é a última etapa de cada um
        def funnelMap = funnels.collectEntries { funnel ->
            def sortedSteps = funnel.steps.sort { it.id }
            [funnel.id, [
                    name      : funnel.name,
                    steps     : sortedSteps.collect { it.id },
                    lastStepId: sortedSteps ? sortedSteps.last()?.id : null
            ]]
        }

        def projectsData = projects.collect { project ->
            def funnelInfo = funnelMap[project.step?.funnel?.id]
            def status = "Em dia"
            def progress = 0

            // Lógica de Status (Trabalho Real)
            if (project.isArchived) {
                status = "Arquivado"
            } else if(project.step?.id == funnelInfo?.lastStepId) {
                status = "Concluído"
                progress = 100
            } else if (project.endDate && project.endDate.isBefore(now)) {
                status = "Atrasado"
            } else if (project.endDate && ChronoUnit.DAYS.between(now, project.endDate) <= 3) {
                status = "Atenção"
            }

            // Cálculo de progresso baseado na etapa
            if (funnelInfo && progress != 100) {
                def stepIndex = funnelInfo.steps.indexOf(project.step?.id)
                if (stepIndex != -1) {
                    progress = Math.round(((stepIndex + 1) / funnelInfo.steps.size()) * 100)
                }
            }

            [
                    id        : project.id,
                    name      : project.name,
                    customer  : project.contact.name,
                    isArchived: project.isArchived,
                    status    : status,
                    progress  : progress,
                    endDate   : project.endDate,
                    userName  : project.user?.name,
                    stepName  : project.step?.name,
                    funnelName: funnelInfo?.name
            ]
        }

        return ResponseEntity.ok().body([
                projects    : projectsData,
                totalUsers  : users.size(),
                totalFunnels: funnels.size()
        ])
    }
}

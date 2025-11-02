package br.com.engepro.api.repository

import br.com.engepro.api.model.Step
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StepRepository extends CrudRepository<Step, Long> {

    List<Step> findByFunnelId(Long funnelId)
}

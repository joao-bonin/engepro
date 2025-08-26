package br.com.engepro.api.repository

import br.com.engepro.api.model.Funnel
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FunnelRepository extends CrudRepository<Funnel, Long> {}

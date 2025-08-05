package br.com.engepro.api.repository

import br.com.engepro.api.model.Funnel
import org.springframework.data.repository.CrudRepository

interface FunnelRepository extends CrudRepository<Funnel, Long> {}

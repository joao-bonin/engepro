package br.com.engepro.api.repository

import br.com.engepro.api.model.Contact
import org.springframework.data.repository.CrudRepository

interface ContactRepository extends CrudRepository<Contact, Long> {}

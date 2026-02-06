package br.com.engepro.api.controller

import br.com.engepro.api.dto.ContactDTO
import br.com.engepro.api.model.Address
import br.com.engepro.api.model.Contact
import br.com.engepro.api.model.User
import br.com.engepro.api.repository.ContactRepository
import groovy.util.logging.Slf4j
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping(path = "/contact")
class ContactController {

    ContactRepository contactRepository

    ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository
    }

    @GetMapping
    ResponseEntity getAllContacts() {
        return ResponseEntity.ok().body(contactRepository.findAll())
    }

    @GetMapping(path = "/{id}")
    ResponseEntity getContactById(@PathVariable Long id) {
        def contact = contactRepository.findById(id)

        if (!contact.isPresent()) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok().body(contact.get())
    }

    @PostMapping
    ResponseEntity createContact(@RequestBody @Valid ContactDTO requestBody,
                                 final BindingResult result, @AuthenticationPrincipal User user) {

        if (!user.hasLevelConfig) return ResponseEntity.unprocessableEntity().build()

        if (result.hasErrors()) {
            log.warn("Contact is not valid: {}", result)
            return ResponseEntity.unprocessableEntity()
                    .body(result.fieldErrors.collect { it.field + ": " + it.defaultMessage })
        }

        Contact contactToCreate = new Contact(name: requestBody.name,
                email: requestBody.email,
                phone: requestBody.phone,
                cnpj: requestBody.cnpj,
                observations: requestBody.observations,
                address: new Address(street: requestBody.address.street, number: requestBody.address.number,
                        city: requestBody.address.city, state: requestBody.address.state,
                        quarter: requestBody.address.quarter, zipCode: requestBody.address.zipCode))

        contactRepository.save(contactToCreate)
        log.info("Contact created: {}", contactToCreate)

        return ResponseEntity.ok().body(contactToCreate)
    }
}

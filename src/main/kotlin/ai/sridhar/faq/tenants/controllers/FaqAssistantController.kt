package ai.sridhar.faq.tenants.controllers

import ai.sridhar.faq.InvalidRequestException
import ai.sridhar.faq.configs.RequestContext
import ai.sridhar.faq.tenants.CreateFaqAssistantRequest
import ai.sridhar.faq.tenants.FaqAssistantDto
import ai.sridhar.faq.tenants.services.FaqAssistantService
import ai.sridhar.faq.tenants.services.GetAllFaqAssistantRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/faqassistant/v1")
class FaqAssistantController(
    private val faqAssistantService: FaqAssistantService,
    private val requestContext: RequestContext
) {

    @PostMapping
    fun create(@RequestBody request: CreateFaqAssistantRequest) : FaqAssistantDto {
        return faqAssistantService.save(request.toEntity()).toDto()
    }

    @GetMapping
    fun getAll() : List<FaqAssistantDto> {
        val request = GetAllFaqAssistantRequest
        return faqAssistantService.get(request).map { it.toDto() }
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) : FaqAssistantDto {
        return faqAssistantService.getOne(id)?.toDto()
            ?: throw InvalidRequestException("FaqAssistant not found")
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) : FaqAssistantDto? {
        val entity = faqAssistantService.getOne(id)
            ?: throw InvalidRequestException("FaqAssistant not found")
        return faqAssistantService.delete(entity).toDto()
    }

}
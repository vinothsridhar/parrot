package ai.sridhar.faq.tenants.services

import ai.sridhar.faq.tenants.entities.FaqAssistant
import ai.sridhar.faq.tenants.entities.FaqAssistantRepository
import org.springframework.stereotype.Service

@Service
class FaqAssistantService(
    private val faqAssistantRepository: FaqAssistantRepository
) {

    fun save(entity: FaqAssistant) : FaqAssistant {
        return faqAssistantRepository.save(entity)
    }

    fun get(request: GetFaqAssistantRequest) : List<FaqAssistant> {
        return when (request) {
            is GetFaqAssistantByIdRequest -> faqAssistantRepository.findByIdIn(request.ids)
            is GetAllFaqAssistantRequest -> faqAssistantRepository.findAll()
        }
    }

    fun getOne(assistantId: Long) : FaqAssistant? {
        return get(
            GetFaqAssistantByIdRequest(
                ids =  setOf(assistantId)
            )
        ).firstOrNull()
    }

    fun delete(entity: FaqAssistant) : FaqAssistant {
        faqAssistantRepository.delete(entity)
        return entity
    }

}

sealed class GetFaqAssistantRequest

data object GetAllFaqAssistantRequest : GetFaqAssistantRequest()

data class GetFaqAssistantByIdRequest(
    val ids: Set<Long>,
) : GetFaqAssistantRequest()
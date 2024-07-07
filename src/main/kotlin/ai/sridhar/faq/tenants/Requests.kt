package ai.sridhar.faq.tenants

import ai.sridhar.faq.tenants.entities.FaqAssistant
import java.time.LocalDateTime

data class CreateFaqAssistantRequest(
    val name: String
) {

    fun toEntity() = FaqAssistant().also {
        it.name = this.name
        it.createdAt = LocalDateTime.now()
    }

}
package ai.sridhar.faq

import ai.sridhar.faq.embedding.entities.Embedding
import ai.sridhar.faq.embedding.entities.EmbeddingStatus
import ai.sridhar.faq.embedding.services.EmbeddingContext
import ai.sridhar.faq.embedding.services.EmbeddingService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EmbeddingListener(
    private val embeddingService: EmbeddingService,
) {

    private val logger = LoggerFactory.getLogger(EmbeddingListener::class.java)

    @Async
    @EventListener
    fun handleEmbeddingEvent(event: EmbeddingEvent) {
        val result  = runCatching {
            embeddingService.embed(event)
            EmbeddingStatus.COMPLETED
        }.onFailure {
            logger.error("Exception in handleEmbeddingEvent", it)
            EmbeddingStatus.FAILED
        }
        event.source.status = result.getOrDefault(EmbeddingStatus.FAILED)
        embeddingService.save(event.source)
    }

}

data class EmbeddingEvent(
    val source: Embedding,
    val context: EmbeddingContext
) : ApplicationEvent(source) {
    override fun toString(): String {
        val embedding = source as Embedding
        return "source: ${embedding.toDto()} - context: ${this.context}"
    }
}
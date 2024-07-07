package ai.sridhar.faq.embedding.services

import ai.sridhar.faq.EmbeddingEvent
import ai.sridhar.faq.FileUtils
import ai.sridhar.faq.embedding.entities.Embedding
import ai.sridhar.faq.embedding.entities.EmbeddingRepository
import ai.sridhar.faq.embedding.entities.EmbeddingStatus
import ai.sridhar.faq.tenants.entities.FaqAssistant
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class EmbeddingService(
    private val vectorStore: VectorStore,
    private val embeddingRepository: EmbeddingRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(EmbeddingService::class.java)

    fun embed(request: EmbeddingRequest): Embedding {
        val embedding = embeddingRepository.save(request.toEntity())
        applicationEventPublisher.publishEvent(EmbeddingEvent(
            context = request.toContext(),
            source = embedding
        ))
        return embedding
    }

    fun embedSync(request: EmbeddingRequest): Embedding {
        val embedding = embeddingRepository.save(request.toEntity())
        embed(EmbeddingEvent(
            context = request.toContext(),
            source = embedding
        ))
        return embedding
    }

    fun save(entity: Embedding) : Embedding {
        return embeddingRepository.save(entity)
    }

    fun embed(event: EmbeddingEvent) {
        logger.info("handling embedding event: $event")
        val context = event.context
        EmbeddingPipeline.execute(context)
        val documents = context.documents.map {
            it.metadata.putAll(
                mapOf(
                    "assistantId" to context.assistant.id,
                    "originalFileName" to context.file.originalFilename,
                    "embeddingId" to event.source.id
                )
            )
            it
        }
        vectorStore.add(documents)
        logger.info("embedding completed successfully: $event")
    }

    fun search(request: EmbeddingSearchRequest) : List<Document> {
        val searchRequest = SearchRequest.defaults()
            .withQuery(request.query)
            .withTopK(request.topK)
            .withFilterExpression("assistantId == ${request.assistantId}")
        return vectorStore.similaritySearch(searchRequest)
    }

    fun get(request: GetEmbeddingRequest) : List<Embedding> {
        return when (request) {
            is GetEmbeddingByAssistantRequest -> embeddingRepository.findByAssistantId(request.assistantId)
            is GetEmbeddingByIdRequest -> embeddingRepository.findAllById(request.ids)
        }
    }

    fun getOne(embeddingId: Long) : Embedding? {
        return get(
            GetEmbeddingByIdRequest(
                ids = setOf(embeddingId)
            )
        ).firstOrNull()
    }

    @Transactional
    fun delete(entity: Embedding) {
        embeddingRepository.delete(entity)
        val searchRequest = SearchRequest.defaults()
            .withQuery(".")
            .withFilterExpression("embeddingId == ${entity.id}")
        val documents = vectorStore.similaritySearch(searchRequest)
        vectorStore.delete(documents.map { it.id })
    }

}

data class EmbeddingSearchRequest(
    val query: String,
    val assistantId: Long,
    val topK: Int = 1
)

data class EmbeddingRequest(
    val name: String?,
    val file: MultipartFile,
    val assistant: FaqAssistant
) {

    fun toContext() = EmbeddingContext(
        file = file,
        assistant = assistant,
        tempFile = FileUtils.multipartFileToTempFile(this.file).absolutePath
    )

    fun toEntity() = Embedding().also {
        it.name = this.name ?: "${this.assistant.name}-${this.file.originalFilename ?: this.file.name}-${LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"
        it.status = EmbeddingStatus.IN_PROGRESS
        it.assistantId = this.assistant.id!!
        it.createdAt = LocalDateTime.now().withNano(0)
    }

}

sealed class GetEmbeddingRequest

data class GetEmbeddingByAssistantRequest(
    val assistantId: Long,
) : GetEmbeddingRequest()

data class GetEmbeddingByIdRequest(
    val ids: Set<Long>,
) : GetEmbeddingRequest()
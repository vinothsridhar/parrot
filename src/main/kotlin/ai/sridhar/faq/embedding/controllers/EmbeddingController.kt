package ai.sridhar.faq.embedding.controllers

import ai.sridhar.faq.InvalidRequestException
import ai.sridhar.faq.configs.RequestContext
import ai.sridhar.faq.embedding.EmbeddingDto
import ai.sridhar.faq.embedding.services.EmbeddingRequest
import ai.sridhar.faq.embedding.services.EmbeddingSearchRequest
import ai.sridhar.faq.embedding.services.EmbeddingService
import ai.sridhar.faq.embedding.services.GetEmbeddingByAssistantRequest
import ai.sridhar.faq.tenants.services.FaqAssistantService
import org.springframework.ai.document.Document
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/embedding/v1")
class EmbeddingController(
    private val faqAssistantService: FaqAssistantService,
    private val embeddingService: EmbeddingService,
    private val requestContext: RequestContext,
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun embed(
        @RequestParam assistantId: Long,
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false, defaultValue = "true") async: Boolean
    ): EmbeddingDto {
        val assistant = faqAssistantService.getOne(assistantId)
            ?: throw InvalidRequestException("Assistant not found")
        val request = EmbeddingRequest(
            name = name,
            file = file,
            assistant = assistant
        )
        val entity = if (async) {
            embeddingService.embed(request)
        } else {
            embeddingService.embedSync(request)
        }
        return entity.toDto()
    }

    @GetMapping("/{assistantId}")
    fun getAll(
        @PathVariable assistantId: Long
    ): List<EmbeddingDto> {
        val entities = embeddingService.get(
            GetEmbeddingByAssistantRequest(
                assistantId = assistantId
            )
        )
        return entities.map { it.toDto() }
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): EmbeddingDto {
        val entity = embeddingService.getOne(id) ?: throw InvalidRequestException("Embedding not found")
        embeddingService.delete(entity)
        return entity.toDto()
    }

    @GetMapping("/search/{assistantId}")
    fun search(
        @RequestParam query: String,
        @PathVariable assistantId: Long
    ): List<Document> {
        return embeddingService.search(
            EmbeddingSearchRequest(
                query = query,
                assistantId = assistantId
            )
        )
    }
}
package ai.sridhar.faq.chat.services

import ai.sridhar.faq.chat.ChatRequest
import ai.sridhar.faq.embedding.services.EmbeddingService
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val embeddingService: EmbeddingService,
    private val promptService: PromptService
) {

    fun chat(request: ChatRequest): String {
        val documents = embeddingService.search(request.toVectorSearch())
        val content = documents.joinToString(" ") { it.content }
        val prompt = """
            question: ${request.query}
            content: $content
        """.trimIndent()
        return promptService.completion(PromptRequest(prompt = prompt))
    }

}

@Service
class PromptService(
    private val chatClient: ChatClient
) {

    fun completion(request: PromptRequest): String {
        return chatClient.prompt().user(request.prompt).call().content()
    }

}

data class PromptRequest(
    val prompt: String
)
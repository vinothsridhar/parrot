package ai.sridhar.faq.chat

import ai.sridhar.faq.embedding.services.EmbeddingSearchRequest

data class ChatRequest(
    val query: String,
    val assistantId: Long
) {

    fun toVectorSearch() = EmbeddingSearchRequest(
        query = this.query,
        assistantId = this.assistantId,
        topK = 2
    )

}
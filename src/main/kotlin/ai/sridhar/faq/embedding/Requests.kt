package ai.sridhar.faq.embedding

data class EmbeddingDocumentRequest(
    val assistantId: Long,
    val documentType: DocumentType
)

enum class DocumentType {
    PDF,
    DOCX
}
package ai.sridhar.faq.embedding.services

import ai.sridhar.faq.DocumentReaderFactory
import ai.sridhar.faq.FileUtils
import ai.sridhar.faq.Step
import ai.sridhar.faq.tenants.entities.FaqAssistant
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.core.io.FileSystemResource
import org.springframework.web.multipart.MultipartFile
import java.io.File


object EmbeddingPipeline {

    private fun steps() = setOf(
        ValidateFileStep(),
        ReaderStep(),
        SplitterStep()
    )

    fun execute(context: EmbeddingContext) {
        steps().map {
            it.process(context)
        }
    }

}

class ValidateFileStep : Step<EmbeddingContext> {
    private val logger = LoggerFactory.getLogger(ValidateFileStep::class.java)
    override fun process(context: EmbeddingContext) {
        logger.info("ValidateFileStep")
    }
}

class ReaderStep : Step<EmbeddingContext> {
    private val logger = LoggerFactory.getLogger(ReaderStep::class.java)
    override fun process(context: EmbeddingContext) {
        logger.info("ReaderStep")
        val file = context.file
        val tempFile = File(context.tempFile)
        val resource = FileSystemResource(tempFile)
        val result = runCatching {
            DocumentReaderFactory.getReader(file.contentType ?: "").read(resource)
        }.onFailure {
            //lets try with tika reader
            DocumentReaderFactory.getReader("invalid").read(resource)
        }.also {
            tempFile.delete()
        }
        context.documents = result.getOrThrow()
    }
}

class SplitterStep : Step<EmbeddingContext> {
    private val logger = LoggerFactory.getLogger(SplitterStep::class.java)
    override fun process(context: EmbeddingContext) {
        logger.info("SplitterStep")
        val tokenTextSplitter = TokenTextSplitter()
        context.documents = tokenTextSplitter.apply(context.documents)
    }
}

data class EmbeddingContext(
    val file: MultipartFile,
    val tempFile: String,
    val assistant: FaqAssistant,
    var documents: List<Document> = listOf()
) {
    override fun toString(): String {
        return "file: $file"
    }
}
package ai.sridhar.faq

import org.springframework.ai.document.Document
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.Resource

interface DocumentReader {
    fun read(resource: Resource) : List<Document>
}

class PdfDocumentReader : DocumentReader {
    override fun read(resource: Resource): List<Document> {
        val config = PdfDocumentReaderConfig.builder()
            .withPageTopMargin(0)
            .withPageExtractedTextFormatter(
                ExtractedTextFormatter.builder()
                    .withNumberOfTopTextLinesToDelete(0)
                    .build()
            )
            .withPagesPerDocument(1)
            .build()
        val pdfReader = PagePdfDocumentReader(resource, config)

        return pdfReader.read()
    }
}

class MyTikaDocumentReader : DocumentReader {
    override fun read(resource: Resource): List<Document> {
        val reader = TikaDocumentReader(resource)
        return reader.read()
    }
}

object DocumentReaderFactory {

    fun getReader(contentType: String) : DocumentReader {
        return when (contentType) {
            "application/pdf" -> PdfDocumentReader()
            else -> MyTikaDocumentReader()
        }
    }

}
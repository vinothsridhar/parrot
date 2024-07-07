package ai.sridhar.faq.embedding.entities

import ai.sridhar.faq.embedding.EmbeddingDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "embedding")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE embedding SET deleted_at = NOW() WHERE id = ?")
class Embedding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "assistant_id", updatable = false)
    var assistantId: Long? = null

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    lateinit var status: EmbeddingStatus

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    fun toDto() = EmbeddingDto(
        id = this.id!!,
        name = this.name,
        status = this.status.name,
        createdAt = this.createdAt?.withNano(0)?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""
    )
}

enum class EmbeddingStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

interface EmbeddingRepository : JpaRepository<Embedding, Long> {
    fun findByAssistantId(assistantId: Long) : List<Embedding>
}
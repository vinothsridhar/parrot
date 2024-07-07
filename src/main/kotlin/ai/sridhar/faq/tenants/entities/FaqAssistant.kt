package ai.sridhar.faq.tenants.entities

import ai.sridhar.faq.tenants.FaqAssistantDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "faq_assistant")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE faq_assistant SET deleted_at = NOW() WHERE id = ?")
class FaqAssistant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "name", nullable = false)
    lateinit var name: String

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    fun toDto() = FaqAssistantDto(
        this.id!!,
        this.name
    )

}

interface FaqAssistantRepository : JpaRepository<FaqAssistant, Long> {

    fun findByIdIn(ids: Set<Long>) : List<FaqAssistant>

}
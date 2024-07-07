package ai.sridhar.faq

import ai.sridhar.faq.chat.ChatRequest
import ai.sridhar.faq.tenants.CreateFaqAssistantRequest
import ai.sridhar.faq.tenants.entities.FaqAssistant
import ai.sridhar.faq.tenants.entities.FaqAssistantRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName


@SpringBootTest
@AutoConfigureMockMvc
class FaqApplicationTests {

	companion object {

		var postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("pgvector/pgvector:pg16"))
			.withDatabaseName("postgres")
			.withUsername("postgres")
			.withPassword("test1234")

		@BeforeAll
		@JvmStatic
		fun start() {
			postgresContainer.start()
		}

		@AfterAll
		@JvmStatic
		fun stop() {
			postgresContainer.stop()
		}

		@DynamicPropertySource
		@JvmStatic
		fun properties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
			registry.add("spring.security.enabled") { false }
		}
	}

	@AfterEach
	fun reset() {
		faqAssistantRepository.deleteAll()
	}

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var objectMapper: ObjectMapper

	@Autowired
	lateinit var faqAssistantRepository: FaqAssistantRepository

	@Test
	fun testFaqAssistant() {
		var request = CreateFaqAssistantRequest(
			"test assistant"
		)

		var result = mockMvc.post("/api/faqassistant/v1") {
			accept = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(request)
			contentType = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
		}.andReturn()

		val faqId = result.findId<Int>()

		mockMvc.get("/api/faqassistant/v1/$faqId") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
			content {
				jsonPath("$.id", notNullValue())
				jsonPath("$.name", equalTo("test assistant"))
			}
		}

	}

	@Test
	fun testEmbedding() {

		val assistant = createFaqAssistant();

		val mockPptFile = ClassPathResource("mocks/mock.docx")
		var file = MockMultipartFile("file", mockPptFile.filename, "application/msword", mockPptFile.inputStream)

		var result = mockMvc.multipart("/api/embedding/v1") {
			param("assistantId", assistant.id!!.toString())
			file(file)
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
		}.andReturn()

		Thread.sleep(20 * 1000)

		mockMvc.get("/api/embedding/v1/search/${assistant.id}") {
			param("query", "knowyourincentive")
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
			content {
				jsonPath("$", hasSize<Int>(1))
			}
		}

		mockMvc.get("/api/embedding/v1/${assistant.id}")
		.andExpect {
			status { is2xxSuccessful() }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$[0].status", equalTo("COMPLETED"))
				jsonPath("$[0].name", startsWith("test assistant-mock.docx"))
			}
		}

		val chatRequest = ChatRequest(
			query = "Please tell about knowyourincentive",
			assistantId = assistant.id!!
		)

		mockMvc.post("/api/chat/v1") {
			content = objectMapper.writeValueAsString(chatRequest)
			contentType = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
		}.andReturn()
	}

	private fun createFaqAssistant() : FaqAssistant {
		val request = CreateFaqAssistantRequest(
			"test assistant"
		)

		val result = mockMvc.post("/api/faqassistant/v1") {
			accept = MediaType.APPLICATION_JSON
			content = objectMapper.writeValueAsString(request)
			contentType = MediaType.APPLICATION_JSON
		}.andExpect {
			status { is2xxSuccessful() }
		}.andReturn()

		val faqId = result.findId<Int>()

		return faqAssistantRepository.findById(faqId.toLong()).get()
	}

}

fun <T> MvcResult.findId(): T = JsonPath.parse(this.response.contentAsString).read("$.id")
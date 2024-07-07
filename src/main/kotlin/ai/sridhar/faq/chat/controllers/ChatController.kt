package ai.sridhar.faq.chat.controllers

import ai.sridhar.faq.InvalidRequestException
import ai.sridhar.faq.chat.ChatRequest
import ai.sridhar.faq.chat.services.ChatService
import ai.sridhar.faq.configs.RequestContext
import ai.sridhar.faq.tenants.services.FaqAssistantService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat/v1")
class ChatController(
    private val faqAssistantService: FaqAssistantService,
    private val chatService: ChatService,
    private val requestContext: RequestContext,
) {

    @PostMapping
    fun chat(
        @RequestBody request: ChatRequest
    ): String {
        faqAssistantService.getOne(request.assistantId)
            ?: throw InvalidRequestException("Assistant not found")
        return chatService.chat(request)
    }
}
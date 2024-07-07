package ai.sridhar.faq.configs

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RequestContext(
    private val requestContext: ThreadLocal<RequestContestStore> = SuppliedInheritableThreadLocal()
) {

    fun setTenantId(tenantId: String) {
        requestContext.get().tenantId = tenantId
    }

    fun setRequestId(requestId: String) {
        requestContext.get().requestId = requestId
    }

    fun setPrincipal(principal: UserPrincipal) {
        requestContext.get().principal = principal
    }

    fun setAuthorization(authorization: String?) {
        requestContext.get().authorization = authorization
    }

    val timezone = requestContext.get().timezone

    val tenantId: String?
        get() = requestContext.get().tenantId

    val principal: UserPrincipal?
        get() = requestContext.get().principal

    val requestId: String?
        get() = requestContext.get().requestId

    val requestTime: LocalDateTime
        get() = requestContext.get().requestTime

    val authorization: String?
        get() = requestContext.get().authorization

    fun clear() {
        requestContext.remove()
    }

}

class SuppliedInheritableThreadLocal : InheritableThreadLocal<RequestContestStore>() {
    override fun initialValue(): RequestContestStore {
        return RequestContestStore()
    }
}

data class RequestContestStore(
    var tenantId: String?,
    var requestId: String = "",
    var timezone: String = "Asia/Kolkata",
    var requestTime: LocalDateTime = LocalDateTime.now(),
    var authorization: String? = ""
) {
    constructor() : this(null)

    var principal: UserPrincipal? = null
}
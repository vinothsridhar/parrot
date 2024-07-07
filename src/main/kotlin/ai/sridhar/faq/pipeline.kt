package ai.sridhar.faq

interface Step<C> {
    fun process(context: C)
}

open class Pipeline<C>(
    private val steps: Set<Step<C>>,
) {
    fun execute(context: C) {
        this.steps.map {
            it.process(context)
        }
    }

}
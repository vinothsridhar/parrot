package ai.sridhar.faq

interface LocalCache<T, V> {
    fun clear()
    fun set(key: T, value: V)
    fun get(key: T) : V?
}
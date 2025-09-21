package ir.bamap.blu.mapper

import kotlin.reflect.KClass

class GeneralMapper : BluMapper<Any, Any>(Any::class, Any::class) {

    final inline fun <reified T : Any> mapAll(
        sources: Collection<Any>,
        ignoreProperties: Collection<String> = emptySet(),
        initProperties: Map<String, Any?> = emptyMap()
    ): List<T> {
        return sources.map { map<T>(it, ignoreProperties, initProperties) }
    }

    final inline fun <reified T : Any> map(
        source: Any,
        ignoreProperties: Collection<String> = emptySet(),
        initProperties: Map<String, Any?> = emptyMap()
    ): T {
        return map(source, T::class, ignoreProperties, initProperties)
    }

    fun <T : Any> map(
        source: Any,
        targetClass: KClass<T>,
        ignoreProperties: Collection<String> = emptySet(),
        initProperties: Map<String, Any?> = emptyMap(),
    ): T {
        return super.map(source, targetClass, initProperties, ignoreProperties)
    }

    fun <T : Any> update(
        entity: T,
        dto: Any,
        ignoreProperties: Collection<String> = emptySet(),
        initProperties: Map<String, Any?> = emptyMap(),
    ): T {
        return updateBySetter(entity, dto, initProperties, ignoreProperties)
    }
}
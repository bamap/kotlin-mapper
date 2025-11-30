package ir.bamap.blu.mapper

import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

abstract class BluMapper<Entity : Any, DTO : Any>(
    protected open val entityClass: KClass<Entity>,
    protected open val dtoClass: KClass<DTO>
) {

    private val logger = LoggerFactory.getLogger(BluMapper::class.java)

    protected fun <T : Any> mapByPrimaryConstructor(
        source: Any,
        targetClass: KClass<T>,
        initProperties: Map<String, Any?> = emptyMap(),
        ignoreProperties: Collection<String> = emptySet()
    ): T {
        val targetConstructor = targetClass.primaryConstructor
            ?: error("Target class '${targetClass.simpleName}' must have a primary constructor")

        val sourceProperties = source::class.memberProperties.associateBy { it.name }

        val targetParameters = targetConstructor.parameters
            .filter { sourceProperties[it.name] != null }
            .filter { !ignoreProperties.contains(it.name) }
            .associateWith { (sourceProperties[it.name] as KProperty1<Any, *>) }

        // TODO
//        val collectionParameters = parameters.filter { Collection::class.java.isAssignableFrom(it.key.type.jvmErasure.java)}
        val simpleParameters =
            targetParameters.filter { !Collection::class.java.isAssignableFrom(it.key.type.jvmErasure.java) }

        val args = mutableMapOf<KParameter, Any?>()
        targetConstructor.parameters
            .filter { initProperties.containsKey(it.name) }
            .forEach {
                args[it] = initProperties[it.name]
            }

        targetParameters.forEach { (param, srcProperty) ->
            if (initProperties.containsKey(srcProperty.name)) {
                args[param] = initProperties[srcProperty.name]
                return@forEach
            }
        }
        simpleParameters
            .filter { !initProperties.containsKey(it.key.name) }
            .forEach { (param, srcProperty) ->

                val accessible = srcProperty.isAccessible
                srcProperty.isAccessible = true
                val value = srcProperty.get(source)

                val propertyName = param.name ?: srcProperty.name
                args[param] = convertValue(value, param.type.jvmErasure, propertyName, propertyName)
                srcProperty.isAccessible = accessible
            }

        try {
            return targetConstructor.callBy(args)
        } catch (exception: RuntimeException) {
            logger.error("Error create an instance of ${targetClass.simpleName} by primary constructor", exception)
            throw exception
        }
    }

    protected fun <T : Any> updateBySetter(
        target: T,
        source: Any,
        initProperties: Map<String, Any?> = emptyMap(),
        ignoreProperties: Collection<String> = emptySet()
    ): T {
        val sourceProperties = source::class.memberProperties.associateBy { it.name }
        val targetProperties = target::class.memberProperties
            .filterIsInstance<KMutableProperty1<T, Any?>>()

        targetProperties
            .filter { !(ignoreProperties.contains(it.name)) }
            .forEach { targetProperty ->
                if (initProperties.containsKey(targetProperty.name)) {
                    targetProperty.setter.call(target, initProperties[targetProperty.name])
                    return@forEach
                }
                val propertyName = targetProperty.name
                val sourceProperty = sourceProperties[propertyName] ?: return@forEach
                val accessible = sourceProperty.isAccessible
                sourceProperty.isAccessible = true
                val value = (sourceProperty as KProperty1<Any, *>).get(source)
                val converted = convertValue(value, targetProperty.returnType.jvmErasure, propertyName, propertyName)
                targetProperty.setter.call(target, converted)
                sourceProperty.isAccessible = accessible
            }

        return target
    }

    protected fun <T : Any> map(
        source: Any,
        targetClass: KClass<T>,
        initProperties: Map<String, Any?> = emptyMap(),
        ignoreProperties: Collection<String> = emptySet()
    ): T {
        val target = mapByPrimaryConstructor(source, targetClass, initProperties, ignoreProperties)

        updateBySetter(target, source, initProperties, ignoreProperties)

        return target
    }

    protected open fun convertValue(
        sourceValue: Any?,
        targetClass: KClass<*>,
        sourcePropertyName: String,
        targetPropertyName: String
    ): Any? {
        if (sourceValue == null) return null
        if (targetClass == sourceValue::class) return sourceValue

        if (targetClass.primaryConstructor == null) return sourceValue
        if (isSimpleType(sourceValue::class)) return sourceValue

        return map(sourceValue, targetClass)
    }

    protected open fun isSimpleType(kClass: KClass<*>): Boolean {
        return kClass.java.isPrimitive ||
                kClass == String::class ||
                Number::class.java.isAssignableFrom(kClass.java) ||
                kClass == Boolean::class ||
                kClass == LocalDateTime::class
    }
}
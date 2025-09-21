package ir.bamap.blu.mapper

import kotlin.reflect.KClass

abstract class SimpleBluMapper<Entity : Any, DTO : Any>(
    entityClass: KClass<Entity>,
    dtoClass: KClass<DTO>
) : BluMapper<Entity, DTO>(entityClass, dtoClass) {

    open fun <T : Entity> update(entity: T, dto: DTO): T {
        return updateBySetter(entity, dto, emptyMap(), setOf("id"))
    }

    open fun mapDto(dto: DTO, isNew: Boolean = true): Entity {
        return if (isNew)
            map(dto, entityClass, emptyMap(), listOf("id"))
        else
            map(dto, entityClass)
    }

    open fun mapEntity(entity: Entity): DTO = map(entity, dtoClass)

    open fun mapDtos(dtos: List<DTO>, isNew: Boolean): List<Entity> = dtos.map { mapDto(it, isNew) }
    open fun mapEntities(entities: List<Entity>): List<DTO> = entities.map { mapEntity(it) }
}

# BluMapper

A lightweight, reflection-based object mapper for Kotlin. Map between data classes, entities, and DTOs with ease.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ir.bamap.blu/kt-mapper/badge.svg)](https://central.sonatype.com/artifact/ir.bamap.blu/kt-mapper)

## Features

- 🔄 Bidirectional mapping: DTO ↔ Entity
- 🚫 Ignore specific properties
- ✨ Override source values with `initProperties`
- ➕ Update existing objects using setters
- 📦 Batch mapping (`mapAll`)
- 🧩 Automatic nested object mapping
- 🎯 Supports Kotlin data classes and primary constructors

## Compatibility

| Kotlin Mapper | Kotlin  | Java |
|---------------|---------|------|
| 1.0.x         | 1.9.25  | 17   |

## Installation

### Maven (`pom.xml`)

```xml
<dependency>
    <groupId>ir.bamap.blu</groupId>
    <artifactId>kt-mapper</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
implementation("ir.bamap.blu:kt-mapper:1.0.1")
```

### Gradle (Groovy DSL)

```groovy
implementation 'ir.bamap.blu:kt-mapper:1.0.1'
```

## Quick Start

```kotlin
val mapper = GeneralMapper()

data class Person(
    val id: Long,
    val name: String,
    val age: Int
)

data class PersonDto(
    val id: Long,
    val name: String,
    val age: Int?
)

val person = Person(1L, "Mohammad", 30)
val dto = mapper.map<PersonDto>(person)

println(dto.name) // John Doe
```

## Examples

### Ignore Properties

```kotlin
val dtoNoId = mapper.map<PersonDto>(
    person,
    ignoreProperties = setOf("id")
)
// dtoNoId.id uses default value (0L or null)
```

### initProperties (Overrides)

```kotlin
val dtoOverridden = mapper.map<PersonDto>(
    person,
    initProperties = mapOf("age" to 99)
)
assertEquals(99, dtoOverridden.age)
```

### Combine ignoreProperties and initProperties

```kotlin
val dtoCustom = mapper.map<PersonDto>(
    person,
    ignoreProperties = setOf("id"),
    initProperties = mapOf("age" to null)
)
// id ignored (default), age = null
```

### Update Existing Object

```kotlin
val entity = Person(999L, "", 0)
mapper.update(entity, dto, ignoreProperties = setOf("id"))
// entity.id remains 999L, other fields updated
```

### Batch Mapping (mapAll)

```kotlin
val persons = listOf(
    Person(1L, "John", 30),
    Person(2L, "Jane", 25)
)
val dtos = mapper.mapAll<PersonDto>(
    persons,
    ignoreProperties = setOf("id")
)
```

### Nested Objects

```kotlin
data class Address(val street: String)

data class Person(val address: Address)

data class PersonDto(val address: Address)

val person = Person(Address("123 Main St"))
val dto = mapper.map<PersonDto>(person)
assertEquals("123 Main St", dto.address.street)
```

## Convenience Class: SimpleBluMapper

For type-safe, reusable mappers:

```kotlin
class PersonMapper : SimpleBluMapper<Person, PersonDto>(
    Person::class, PersonDto::class
)

val personMapper = PersonMapper()
val dto = personMapper.mapEntity(person)
val entity = personMapper.mapDto(dto, isNew = true) // ignores id for new
```

## License

[MIT License](LICENSE)

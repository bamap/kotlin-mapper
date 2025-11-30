package ir.bamap.blu.mapper

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

data class Address(val street: String)

data class PersonDto(
    val name: String,
    val age: Int,
    val address: Address
)

data class Person(
    val name: String,
    val age: Int = 0,
    val address: Address? = null
)

class GeneralMapperTest {

    private val mapper = GeneralMapper()

    @Test
    fun `should map single DTO to Entity`() {
        val address = Address("123 Main St")
        val dto = PersonDto("Alice", 25, address)
        val person: Person = mapper.map(dto)
        assertEquals("Alice", person.name)
        assertEquals(25, person.age)
        assertSame(address, person.address)
    }

    @Test
    fun `should map single DTO to Entity using non-reified`() {
        val address = Address("456 Oak Ave")
        val dto = PersonDto("Bob", 30, address)
        val person: Person = mapper.map(dto, Person::class)
        assertEquals("Bob", person.name)
        assertEquals(30, person.age)
        assertSame(address, person.address)
    }

    @Test
    fun `should map list of DTOs to list of Entities`() {
        val address1 = Address("123 Main St")
        val address2 = Address("456 Oak Ave")
        val dtos = listOf(
            PersonDto("Alice", 25, address1),
            PersonDto("Bob", 30, address2)
        )
        val persons: List<Person> = mapper.mapAll(dtos)
        assertEquals(2, persons.size)
        assertEquals("Alice", persons[0].name)
        assertEquals(25, persons[0].age)
        assertSame(address1, persons[0].address)
        assertEquals("Bob", persons[1].name)
        assertEquals(30, persons[1].age)
        assertSame(address2, persons[1].address)
    }

    @Test
    fun `should update entity using DTO`() {
        val address = Address("123 Main St")
        val person = Person("OldName", 40, address)
        val dto = PersonDto("NewName", 50, address)
        val updated = mapper.update(person, dto)
        assertSame(person, updated)
        assertEquals("NewName", person.name)
        assertEquals(50, person.age)
        assertSame(address, person.address)
    }

    @Test
    fun `should update entity ignoring property`() {
        val address = Address("123 Main St")
        val person = Person("KeepName", 40, address)
        val dto = PersonDto("IgnoreName", 50, address)
        val updated = mapper.update(person, dto, ignoreProperties = setOf("name"))
        assertSame(person, updated)
        assertEquals("KeepName", person.name)
        assertEquals(50, person.age)
        assertSame(address, person.address)
    }

    @Test
    fun `should use initProperties in map`() {
        val address = Address("789 Pine Rd")
        val dto = PersonDto("Alice", 25, address)
        val person: Person = mapper.map(
            dto,
            initProperties = mapOf(
                "name" to "InitName",
                "age" to 100
            )
        )
        assertEquals("InitName", person.name)
        assertEquals(100, person.age)
        assertSame(address, person.address)
    }

    @Test
    fun `should ignore property in map using default`() {
        val address = Address("789 Pine Rd")
        val dto = PersonDto("IgnoreAgeDto", 99, address)
        val person: Person = mapper.map(
            dto,
            ignoreProperties = setOf("age")
        )
        assertEquals("IgnoreAgeDto", person.name)
        assertEquals(0, person.age)
        assertSame(address, person.address)
    }
}

package ir.bamap.blu.mapper

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

data class Address(val street: String)

data class PersonDto(
    val name: String,
    val age: Int,
    val address: Address
)

data class Person(
    val name: String,
    val age: Int? = 0,
    val address: Address? = null
)

class Personnel(
    var id: Long = 0L,
    var name: String = "",
    var age: Int = 0
)

data class PersonnelDto(
    val id: Long? = null,
    val name: String = "",
    val age: Int = 0
)

class BluMapperTest {

    private val mapper = GeneralMapper()

    @Test
    fun `map copies all properties by default`() {
        val source = Personnel(1L, "John", 30)
        val dto = mapper.map<PersonnelDto>(source)
        assertEquals("John", dto.name)
        assertEquals(30, dto.age)
        assertEquals(1L, dto.id)
    }

    @Test
    fun `map ignores id property`() {
        val source = Personnel(1L, "John", 30)
        val dto = mapper.map<PersonnelDto>(source, ignoreProperties = setOf("id"))
        assertEquals("John", dto.name)
        assertEquals(30, dto.age)
        assertNull(dto.id)
    }

    @Test
    fun `map ignores multiple properties`() {
        val source = Personnel(1L, "John", 30)
        val dto = mapper.map<PersonnelDto>(source, ignoreProperties = setOf("id", "name"))
        assertEquals("", dto.name)
        assertEquals(0, dto.age)
        assertNull(dto.id)
    }

    @Test
    fun `update ignores specified properties`() {
        val entity = Personnel(1L, "old", 10)
        val sourceDto = PersonnelDto(99L, "new", 25)
        mapper.update(entity, sourceDto, ignoreProperties = setOf("id"))
        assertEquals(1L, entity.id)
        assertEquals("new", entity.name)
        assertEquals(25, entity.age)
    }

    @Test
    fun `mapAll ignores properties`() {
        val sources = listOf(
            Personnel(1L, "John", 30),
            Personnel(2L, "Jane", 25)
        )
        val dtos = mapper.mapAll<PersonnelDto>(sources, ignoreProperties = setOf("id"))
        assertEquals(2, dtos.size)
        with(dtos[0]) {
            assertNull(id)
            assertEquals("John", name)
            assertEquals(30, age)
        }
        with(dtos[1]) {
            assertNull(id)
            assertEquals("Jane", name)
            assertEquals(25, age)
        }
    }
}

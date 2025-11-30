package ir.bamap.blu.mapper

import ir.bamap.blu.mapper.model.Address
import ir.bamap.blu.mapper.model.Person
import ir.bamap.blu.mapper.model.PersonDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BluMapperTest {

    private val mapper = GeneralMapper()

    @Test
    fun `map copies all properties by default`() {
        val source = Person(1L, "Mohammad", 63, Address("Mecca"))
        val dto = mapper.map<PersonDto>(source)
        assertEquals(source.id, dto.id)
        assertEquals(source.age, dto.age)
        assertEquals(source.name, dto.name)
        assertEquals(source.address?.street, dto.address.street)
    }

    @Test
    fun `map ignores id property`() {
        val source = Person(100, "Mohammad", 63, Address("Mecca"))
        val dto = mapper.map<PersonDto>(source, ignoreProperties = setOf("id"))
        assertNotEquals(source.id, dto.id)
    }

    @Test
    fun `map ignores multiple properties`() {
        val source = Person(100, "Mohammad", 63, Address("Mecca"))
        val dto = mapper.map<PersonDto>(source, ignoreProperties = setOf("id", "name"))
        assertNotEquals(source.id, dto.id)
        assertNotEquals(source.name, dto.name)
    }

    @Test
    fun `map uses initProperties to override source value`() {
        val source = Person(1L, "sourceName", 30, null)
        val dto = mapper.map<PersonDto>(source, initProperties = mapOf("age" to 99))
        assertEquals("sourceName", dto.name)
        assertEquals(1L, dto.id)
        assertEquals(99, dto.age)
    }

    @Test
    fun `map uses initProperties to set null`() {
        val source = Person(1L, "sourceName", 30, null)
        val dto = mapper.map<PersonDto>(source, initProperties = mapOf("age" to null))
        assertEquals(null, dto.age)
    }

    @Test
    fun `mapAll uses initProperties`() {
        val sources = listOf(
            Person(1L, "John", 30, null),
            Person(2L, "Jane", 25, null)
        )
        val dtos = mapper.mapAll(sources, initProperties = mapOf("name" to "overridden"))
        assertEquals(2, dtos.size)
        assertEquals("overridden", dtos[0].name)
        assertEquals("overridden", dtos[1].name)
    }

    @Test
    fun `map combines initProperties and ignoreProperties`() {
        val source = Person(1L, "sourceName", 30, null)
        val dto = mapper.map<PersonDto>(
            source,
            ignoreProperties = setOf("id"),
            initProperties = mapOf("age" to 99)
        )
        assertEquals(1L, dto.id) // default since ignored
        assertEquals("sourceName", dto.name)
        assertEquals(99, dto.age)
    }
}

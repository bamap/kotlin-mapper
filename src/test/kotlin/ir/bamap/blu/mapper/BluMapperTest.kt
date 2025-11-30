package ir.bamap.blu.mapper

import org.junit.jupiter.api.Test

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

class BluMapperTest {

    private val mapper = GeneralMapper()

    @Test
    fun `error`() {
        val entry = Person("name", null)
        val dto = mapper.map<PersonDto>(entry)
    }
}

package ir.bamap.blu.mapper.model


class PersonDto(
    val id: Long = 1,
    val name: String = "Ali",
    val age: Int? = null,
    val address: Address = Address("Street")
) {
}
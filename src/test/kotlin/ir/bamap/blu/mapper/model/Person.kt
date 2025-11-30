package ir.bamap.blu.mapper.model

data class Person(
    val id: Long = 0,
    val name: String = "Ali",
    val age: Int = 0,
    val address: Address = Address("Mecca")
) {
}
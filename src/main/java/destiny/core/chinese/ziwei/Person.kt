/**
 * Created by smallufo on 2018-06-06.
 */
package destiny.core.chinese.ziwei

data class Address(
  var city: String? = null,
  var street: String? = null)

data class Person(
  val name: String,
  var age: Int? = null ,
  var address: Address?=null)


fun Person.address(block: Address.() -> Unit) {
  this.address = Address().apply(block)
}

fun person(block: Person.() -> Unit) = Person("Alice").apply(block)



fun main(args: Array<String>) {
  val p = Person("Alice").apply {
    age = 12
    address {
      city = "Taipei"
      street = "忠孝東路"
    }
  }

  println(p)

  val person = person {
    age = 12
  }

  println(person)
}
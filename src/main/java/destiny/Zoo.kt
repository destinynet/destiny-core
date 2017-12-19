package destiny

sealed class Animal(val type: String) {
  data class Cat(val color: String) : Animal("cat")
  data class Dog(val mood: String, val ownersName: String) : Animal("dog")
}

abstract class AnimalDescriptor<out T : Animal>(val a: T) {
  fun sayHello() : String {
    return "Hello : " + getSummary()
  }
  abstract fun getSummary(): String
}

class CatDescriptor(a: Animal.Cat) : AnimalDescriptor<Animal.Cat>(a) {
  override fun getSummary(): String {
    return "The color of this ${a.type} is ${a.color}"
  }
}

class DogDescriptor(a: Animal.Dog) : AnimalDescriptor<Animal.Dog>(a) {
  override fun getSummary(): String {
    return "${a.ownersName}'s ${a.type} is ${a.mood}"
  }
}

class ADescriptor(a : Animal) : AnimalDescriptor<Animal>(a) {
  override fun getSummary(): String {
    return "type = ${a.type} "
  }
}

fun main(args: Array<String>) {

  fun hello(a: Animal): String {
    return when (a) {
      is Animal.Cat -> CatDescriptor(a)
      is Animal.Dog -> DogDescriptor(a)
    }.sayHello()
  }

  val kitten = Animal.Cat("yellow")
  val doggy = Animal.Dog("happy", "John")

  println(hello(kitten))
  println(hello(doggy))



  println(ADescriptor(kitten).getSummary())
}

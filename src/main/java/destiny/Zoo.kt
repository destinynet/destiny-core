package destiny

sealed class Animal(val type: String) {
  data class Cat(val color: String) : Animal("cat")
  data class Dog(val mood: String, val ownersName: String) : Animal("dog")
}

abstract class AnimalDescriptor<out T : Animal>(val a: T) {
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


fun main(args: Array<String>) {

  fun summary(a: Animal): String {
    return when (a) {
      is Animal.Cat -> CatDescriptor(a)
      is Animal.Dog -> DogDescriptor(a)
    }.getSummary()
  }

  val kitten = Animal.Cat("yellow")
  val doggy = Animal.Dog("happy", "John")

  println(summary(kitten))
  println(summary(doggy))
}

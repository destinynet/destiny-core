package destiny

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


sealed class Animal(val type: String) {
  data class Cat(val color: String) : Animal("cat")
  data class Dog(val mood: String, val ownersName: String) : Animal("dog")
}

abstract class AnimalDescriptor<out T : Animal>(val a: T) {
  fun sayHello(): String {
    return "Hello : " + getSummary()
  }

  abstract fun getSummary(): String
}


class CatDescriptor(a: Animal.Cat) : AnimalDescriptor<Animal.Cat>(a) {

  private val localeString: String by MyDelegate()

  override fun getSummary(): String {
    return "The color of this ${a.type} is ${a.color}"
  }
}

class MyDelegate : ReadOnlyProperty<CatDescriptor, String> {
  override fun getValue(thisRef: CatDescriptor, property: KProperty<*>): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}

class DogDescriptor(a: Animal.Dog) : AnimalDescriptor<Animal.Dog>(a) {
  override fun getSummary(): String {
    return "${a.ownersName}'s ${a.type} is ${a.mood}"
  }
}

class ADescriptor(a: Animal) : AnimalDescriptor<Animal>(a) {
  override fun getSummary(): String {
    return "type = ${a.type} "
  }
}


fun Animal.toString(): String {
  return "Animal : $this"
}

interface ILocaleString2 {

  fun toString(lang: String, country: String?, variant: String?): String
}


class LocaleAnimal(c: Animal) : ILocaleString2 {
  override fun toString(lang: String, country: String?, variant: String?): String {
    return "Animal : ${this}"
  }
}

class DerivedCat(localString2: ILocaleString2) : ILocaleString2 by localString2



fun main(args: Array<String>) {

  fun hello(a: Animal): String {
    return when (a) {
      is Animal.Cat -> CatDescriptor(a)
      is Animal.Dog -> DogDescriptor(a)
    }.sayHello()
  }

  val kitten = Animal.Cat("yellow")
  val doggy = Animal.Dog("happy", "John")

  println("kitten = {$kitten}")


  val dc = DerivedCat(LocaleAnimal(Animal.Cat("Meow")))
  println(dc.toString("zh", "tw", null))


  println(hello(kitten))
  println(hello(doggy))

  println(ADescriptor(kitten).getSummary())
}

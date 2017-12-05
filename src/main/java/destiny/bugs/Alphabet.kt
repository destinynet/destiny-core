package destiny.bugs

sealed class Alphabet(val name: String) {
  object A : Alphabet("A")
  object B : Alphabet("B")
  object C : Alphabet("C")
  object D : Alphabet("D")
  object E : Alphabet("E")

  companion object {
    val array = arrayOf(A, B, C, D, E)
    val list = listOf(A, B, C, D, E)
  }

  override fun toString(): String {
    return name
  }
}
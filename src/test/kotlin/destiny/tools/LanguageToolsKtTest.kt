/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.tools

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LanguageToolsKtTest {

  open class Animal<T>
  class Cat : Animal<String>()
  class Dog : Animal<Int>()
  class Bird // 沒有泛型父類別
  class Fish : Animal<Fish>()

  @Nested
  inner class SearchByGenericSuperclassTest {
    @Test
    fun 找到匹配的泛型類型() {
      // Arrange
      val collection = listOf(
        Cat(),
        Dog(),
        Bird()
      )

      // Act
      val result = collection.searchByGenericSuperclass(String::class.java)

      // Assert
      assertTrue(result is Cat)
    }

    @Test
    fun 找不到匹配的泛型類型時返回null() {
      // Arrange
      val collection = listOf(
        Cat(),
        Dog(),
        Bird()
      )

      // Act
      val result = collection.searchByGenericSuperclass(Boolean::class.java)

      // Assert
      assertNull(result)
    }

    @Test
    fun 處理空集合() {
      // Arrange
      val emptyCollection = emptyList<Animal<*>>()

      // Act
      val result = emptyCollection.searchByGenericSuperclass(String::class.java)

      // Assert
      assertNull(result)
    }

    @Test
    fun 處理沒有泛型父類別的元素() {
      // Arrange
      val collection = listOf(Bird())

      // Act
      val result = collection.searchByGenericSuperclass(String::class.java)

      // Assert
      assertNull(result)
    }

    @Test
    fun 處理自引用泛型類型() {
      // Arrange
      val collection = listOf(Fish())

      // Act
      val result = collection.searchByGenericSuperclass(Fish::class.java)

      // Assert
      assertTrue(result is Fish)
    }

    @Test
    fun 處理混合類型的集合() {
      // Arrange
      val mixedCollection = listOf(
        Cat(),
        Dog(),
        Bird(),
        Fish(),
        null
      )

      // Act
      val result = mixedCollection.searchByGenericSuperclass(String::class.java)

      // Assert
      assertTrue(result is Cat)
    }
  }
}

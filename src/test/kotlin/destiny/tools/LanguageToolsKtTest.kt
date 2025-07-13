/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.tools

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LanguageToolsKtTest {

  @Nested
  inner class DoubleTruncateTest {

    @Test
    fun testExactRound() {
      assertEquals("2.00", 1.99999999.truncateToString(2))
      assertEquals("0.00", 0.0000001.truncateToString(2))
      assertEquals("1.00", 0.99999999.truncateToString(2))
      assertEquals("100.00", 99.9999999.truncateToString(2))
    }

    @Test
    fun testTruncateWithoutRound() {
      assertEquals("1.99", 1.994.truncateToString(2))
      assertEquals("0.12", 0.12999.truncateToString(2))
      assertEquals("12.34", 12.3456.truncateToString(2))
      assertEquals("359.99", 359.999.truncateToString(2))
    }

    @Test
    fun testEdgeCases() {
      assertEquals("100", 100.0.truncateToString(0))
      assertEquals("0", 0.0.truncateToString(0))
      assertEquals("0.0", 0.04.truncateToString(1))
      assertEquals("0.00", 0.004.truncateToString(2))
      assertEquals("0.00", 0.0000001.truncateToString(2))
    }

    @Test
    fun testEpsilonEffect() {
      // 使用不同 epsilon 會影響判斷是否 round
      val value = 1.995
      assertEquals("1.99", value.truncateToString(2, epsilon = 1e-3))
      assertEquals("2.00", value.truncateToString(2, epsilon = 0.6))
    }

    @Test
    fun testInvalidInput() {
      try {
        (-1.0).truncateToString(-1)
        assert(false) { "Should throw exception for negative n" }
      } catch (e: IllegalArgumentException) {
        // pass
      }

      try {
        1.0.truncateToString(6)
        assert(false) { "Should throw exception for n > 5" }
      } catch (e: IllegalArgumentException) {
        // pass
      }
    }
  }


  @Nested
  inner class SearchByGenericSuperclassTest {


    open inner class Animal<T>
    inner class Cat : Animal<String>()
    inner class Dog : Animal<Int>()
    inner class Bird // 沒有泛型父類別
    inner class Fish : Animal<Fish>()

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

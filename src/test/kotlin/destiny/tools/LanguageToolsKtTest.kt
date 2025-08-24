/**
 * Created by smallufo on 2025-01-13.
 */
package destiny.tools

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
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

  @Nested
  inner class ToKTypeTest {

    // 輔助用的容器類別，以便透過反射取得各種泛型 Type
    inner class TypeContainer {
      @Suppress("unused")
      lateinit var simpleList: List<String>
      @Suppress("unused")
      lateinit var nestedMap: Map<String, List<Int>>
      @Suppress("unused")
      lateinit var unboundedWildcardList: List<*>
      @Suppress("unused")
      lateinit var upperBoundedWildcardList: List<Number>
      @Suppress("unused")
      lateinit var genericArray: Array<List<String>>
    }

    private fun getGenericType(fieldName: String): Type {
      return TypeContainer::class.java.getDeclaredField(fieldName).genericType
    }

    @Test
    fun `簡單的 Class 應轉換為 non-nullable KType`() {
      val type: Type = String::class.java
      val kType = type.toKType()
      assertEquals(String::class.createType(nullable = false), kType)
    }

    @Test
    fun `簡單的參數化型別應正確轉換`() {
      val type = getGenericType("simpleList") // List<String>
      val kType = type.toKType()
      val expectedKType = List::class.createType(
        arguments = listOf(KTypeProjection.invariant(String::class.createType()))
      )
      assertEquals(expectedKType.toString(), kType.toString())
    }

    @Test
    fun `巢狀的參數化型別應正確轉換`() {
      val type = getGenericType("nestedMap") // Map<String, List<Int>>
      val kType = type.toKType()
      val expectedKType = Map::class.createType(
        arguments = listOf(
          KTypeProjection.invariant(String::class.createType()),
          KTypeProjection.invariant(
            List::class.createType(
              arguments = listOf(KTypeProjection.invariant(Int::class.createType()))
            )
          )
        )
      )
      assertEquals(expectedKType.toString(), kType.toString())
    }

    @Test
    fun `無邊界萬用字元應轉換為其上界 (Any)`() {
      val parameterizedType = getGenericType("unboundedWildcardList") as ParameterizedType // List<*>
      val wildcardType = parameterizedType.actualTypeArguments[0] // ?
      val kType = wildcardType.toKType()
      // 對於 '?'，其上界是 Object，對應到 Kotlin 的 Any
      assertEquals(Any::class.createType(), kType)
    }

    @Test
    fun `有上界的萬用字元應轉換為其上界`() {
      val parameterizedType = getGenericType("upperBoundedWildcardList") as ParameterizedType // List<? extends Number>
      val wildcardType = parameterizedType.actualTypeArguments[0] // ? extends Number
      val kType = wildcardType.toKType()
      assertEquals(Number::class.createType(), kType)
    }

    @Test
    fun `不支援的型別 (GenericArrayType) 應拋出例外`() {
      val type = getGenericType("genericArray") // 在此上下文中，Array<List<String>> 是一個 GenericArrayType
      assertTrue(type is java.lang.reflect.GenericArrayType, "The type should be GenericArrayType")

      val exception = assertThrows<IllegalArgumentException> {
        type.toKType()
      }
      assertTrue(exception.message!!.startsWith("Unsupported type:"), "例外訊息應表明型別不被支援")
    }
  }
}

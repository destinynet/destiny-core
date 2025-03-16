/**
 * @author smallufo
 * @date 2005/4/4
 * @time 下午 03:05:36
 */
package destiny.tools.canvas

import destiny.tools.canvas.ColorByte.Companion.validateColor
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.awt.Font
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ColorByteTest {


  @Test
  fun `null input should return null`() {
    val result = validateColor(null)
    assertEquals(null, result)
  }


  private fun validColorsProvider(): Stream<Arguments> = Stream.of(
    // 3-digit hex without hash
    Arguments.of("abc", "#ABC"),
    Arguments.of("123", "#123"),
    Arguments.of("f0a", "#F0A"),

    // 6-digit hex without hash
    Arguments.of("abcdef", "#ABCDEF"),
    Arguments.of("123456", "#123456"),
    Arguments.of("f0a1b2", "#F0A1B2"),

    // 3-digit hex with hash
    Arguments.of("#abc", "#ABC"),
    Arguments.of("#123", "#123"),
    Arguments.of("#f0a", "#F0A"),

    // 6-digit hex with hash
    Arguments.of("#abcdef", "#ABCDEF"),
    Arguments.of("#123456", "#123456"),
    Arguments.of("#f0a1b2", "#F0A1B2"),

    // Mixed case inputs
    Arguments.of("AbC", "#ABC"),
    Arguments.of("aBcDeF", "#ABCDEF"),
    Arguments.of("#AbC", "#ABC"),
    Arguments.of("#aBcDeF", "#ABCDEF")
  )

  @ParameterizedTest
  @MethodSource("validColorsProvider")
  fun `valid colors should be normalized`(input: String, expected: String) {
    val result = validateColor(input)
    assertEquals(expected, result)
  }

  private fun invalidColorsProvider(): Stream<Arguments> = Stream.of(
    // Empty string
    Arguments.of(""),

    // Wrong length
    Arguments.of("ab"),
    Arguments.of("abcd"),
    Arguments.of("abcde"),
    Arguments.of("abcdefg"),
    Arguments.of("#ab"),
    Arguments.of("#abcd"),
    Arguments.of("#abcde"),
    Arguments.of("#abcdefg"),

    // Invalid characters
    Arguments.of("12g"),
    Arguments.of("xyz"),
    Arguments.of("12345g"),
    Arguments.of("#12g"),
    Arguments.of("#xyz"),
    Arguments.of("#12345g"),

    // Non-hex strings
    Arguments.of("hello"),
    Arguments.of("#hello"),
    Arguments.of("rgb(255,0,0)"),
    Arguments.of("rgba(255,0,0,0.5)")
  )

  @ParameterizedTest
  @MethodSource("invalidColorsProvider")
  fun `invalid colors should return original value`(input: String) {
    val result = validateColor(input)
    assertEquals(input, result)
  }


  @Test
  fun testIsSameProperties() {
    var cb1: ColorByte
    var cb2: ColorByte
    //應該相同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //應該相同
    cb1 = ColorByte.of(1.toByte(), "ffffff", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "ffffff", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //應該相同
    cb1 = ColorByte.of(1.toByte(), "abc", "DEF", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "ABC", "def", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //前景不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "green", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //背景不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "black", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體family不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("標楷體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體型態不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.ITALIC, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體大小不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 14), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //網址不同 , 但是網址不納入比對 (耗時)，因此會視為相同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to", null)
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to/", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //title 不同
    cb1 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to/", "AAA")
    cb2 = ColorByte.of(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "https://destiny.to/", "BBB")
    assertTrue(!cb1.isSameProperties(cb2))
  }

}

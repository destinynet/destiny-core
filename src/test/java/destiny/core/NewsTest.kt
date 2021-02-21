/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import destiny.core.News.EastWest
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

internal class NewsTest {

  @Test
  fun testOf() {
    assertSame(NORTH, NorthSouth.of('N'))
    assertSame(NORTH, NorthSouth.of('n'))

    assertSame(SOUTH, NorthSouth.of('S'))
    assertSame(SOUTH, NorthSouth.of('s'))

    assertSame(EAST, EastWest.of('E'))
    assertSame(EAST, EastWest.of('e'))

    assertSame(WEST, EastWest.of('W'))
    assertSame(WEST, EastWest.of('w'))

    assertSame(NORTH, News.of('N'))
    assertSame(NORTH, News.of('n'))
    assertSame(SOUTH, News.of('S'))
    assertSame(SOUTH, News.of('s'))
    assertSame(EAST, News.of('E'))
    assertSame(EAST, News.of('e'))
    assertSame(WEST, News.of('W'))
    assertSame(WEST, News.of('w'))

    assertNull(News.of('x'))
  }

  @Test
  fun testNorthSouthCoordinateString() {
    assertEquals("北緯", NORTH.toCoordinateString(Locale.TAIWAN))
    assertEquals("南緯", SOUTH.toCoordinateString(Locale.TAIWAN))

    assertEquals("北纬", NORTH.toCoordinateString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("南纬", SOUTH.toCoordinateString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("North", NORTH.toCoordinateString(Locale.ENGLISH))
    assertEquals("South", SOUTH.toCoordinateString(Locale.ENGLISH))
  }

  @Test
  fun testNorthSouthString() {
    assertEquals("NORTH", NORTH.toString())
    assertEquals("SOUTH", SOUTH.toString())
  }


  @Test
  fun testEastWestCoordinateString() {
    assertEquals("東經", EAST.toCoordinateString(Locale.TAIWAN))
    assertEquals("西經", WEST.toCoordinateString(Locale.TAIWAN))

    assertEquals("东经", EAST.toCoordinateString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("西经", WEST.toCoordinateString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("East", EAST.toCoordinateString(Locale.ENGLISH))
    assertEquals("West", WEST.toCoordinateString(Locale.ENGLISH))
  }

  @Test
  fun testEastWestString() {
    assertEquals("EAST", EAST.toString())
    assertEquals("WEST", WEST.toString())
  }

  @Test
  fun testLocaleString() {
    assertEquals("北", NORTH.toString(Locale.TAIWAN))
    assertEquals("南", SOUTH.toString(Locale.TAIWAN))
    assertEquals("東", EAST.toString(Locale.TAIWAN))
    assertEquals("西", WEST.toString(Locale.TAIWAN))

    assertEquals("北", NORTH.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("南", SOUTH.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("东", EAST.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("西", WEST.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("N", NORTH.toString(Locale.ENGLISH))
    assertEquals("S", SOUTH.toString(Locale.ENGLISH))
    assertEquals("E", EAST.toString(Locale.ENGLISH))
    assertEquals("W", WEST.toString(Locale.ENGLISH))
  }
}

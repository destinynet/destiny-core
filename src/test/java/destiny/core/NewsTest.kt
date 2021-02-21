/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import destiny.core.News.EastWest
import destiny.core.News.NorthSouth
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NewsTest {

  @Test
  fun testNorthSouthCoordinateString() {
    assertEquals("北緯", NorthSouth.NORTH.toCoordinateString(Locale.TAIWAN))
    assertEquals("南緯", NorthSouth.SOUTH.toCoordinateString(Locale.TAIWAN))

    assertEquals("北纬", NorthSouth.NORTH.toCoordinateString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("南纬", NorthSouth.SOUTH.toCoordinateString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("North", NorthSouth.NORTH.toCoordinateString(Locale.ENGLISH))
    assertEquals("South", NorthSouth.SOUTH.toCoordinateString(Locale.ENGLISH))
  }

  @Test
  fun testNorthSouthString() {
    assertEquals("NORTH", NorthSouth.NORTH.toString())
    assertEquals("SOUTH", NorthSouth.SOUTH.toString())
  }


  @Test
  fun testEastWestCoordinateString() {
    assertEquals("東經", EastWest.EAST.toCoordinateString(Locale.TAIWAN))
    assertEquals("西經", EastWest.WEST.toCoordinateString(Locale.TAIWAN))

    assertEquals("东经", EastWest.EAST.toCoordinateString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("西经", EastWest.WEST.toCoordinateString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("East", EastWest.EAST.toCoordinateString(Locale.ENGLISH))
    assertEquals("West", EastWest.WEST.toCoordinateString(Locale.ENGLISH))
  }

  @Test
  fun testEastWestString() {
    assertEquals("EAST", EastWest.EAST.toString())
    assertEquals("WEST", EastWest.WEST.toString())
  }

  @Test
  fun testLocaleString() {
    assertEquals("北", NorthSouth.NORTH.toString(Locale.TAIWAN))
    assertEquals("南", NorthSouth.SOUTH.toString(Locale.TAIWAN))
    assertEquals("東", EastWest.EAST.toString(Locale.TAIWAN))
    assertEquals("西", EastWest.WEST.toString(Locale.TAIWAN))

    assertEquals("北", NorthSouth.NORTH.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("南", NorthSouth.SOUTH.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("东", EastWest.EAST.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("西", EastWest.WEST.toString(Locale.SIMPLIFIED_CHINESE))

    assertEquals("N", NorthSouth.NORTH.toString(Locale.ENGLISH))
    assertEquals("S", NorthSouth.SOUTH.toString(Locale.ENGLISH))
    assertEquals("E", EastWest.EAST.toString(Locale.ENGLISH))
    assertEquals("W", EastWest.WEST.toString(Locale.ENGLISH))
  }
}

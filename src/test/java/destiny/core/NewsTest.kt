/**
 * Created by smallufo on 2021-02-22.
 */
package destiny.core

import destiny.core.News.*
import java.util.*
import kotlin.test.Test
import kotlin.test.*

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
}

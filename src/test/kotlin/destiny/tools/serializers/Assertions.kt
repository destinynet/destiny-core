/**
 * Created by smallufo on 2023-10-24.
 */
package destiny.tools.serializers

import destiny.core.IBirthData
import destiny.core.IBirthDataNamePlace
import destiny.core.ITimeLoc
import destiny.core.calendar.ILocation
import kotlin.test.assertEquals
import kotlin.test.assertSame

object Assertions {

  fun assertLocEquals(expected: ILocation, actual: ILocation) {
    assertEquals(expected.lat, actual.lat)
    assertEquals(expected.lng, actual.lng)
    assertEquals(expected.tzid, actual.tzid)
    assertEquals(expected.minuteOffset, actual.minuteOffset)
    assertEquals(expected.altitudeMeter, actual.altitudeMeter)
  }

  fun assertTimeLocEquals(expected: ITimeLoc, actual: ITimeLoc) {
    assertEquals(expected.time, actual.time)
    assertLocEquals(expected.location, actual.location)
  }

  fun assertBirthDataEquals(expected: IBirthData, actual: IBirthData) {
    assertSame(expected.gender, actual.gender)
    assertTimeLocEquals(expected, actual)
  }

  fun assertBdnpEquals(expected: IBirthDataNamePlace, actual: IBirthDataNamePlace) {
    assertBirthDataEquals(expected, actual)
    assertEquals(expected.name, actual.name)
    assertEquals(expected.place, actual.place)
  }
}

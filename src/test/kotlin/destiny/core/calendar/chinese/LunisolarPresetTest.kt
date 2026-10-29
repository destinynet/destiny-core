/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import destiny.tools.Lang
import destiny.tools.getTitle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LunisolarPresetTest {

  @Test
  fun roundTrip() {
    LunisolarPreset.entries.forEach { preset ->
      assertEquals(preset, LunisolarPreset.of(preset.applyTo(LunisolarConfig())))
    }
    assertEquals(LunisolarPreset.CIVIL, LunisolarPreset.of(LunisolarConfig()))
  }

  /** civil 由市場決定，套用組合不能改掉它 */
  @Test
  fun applyToKeepsCivil() {
    val korea = LunisolarConfig(civil = CivilStandard.KOREA)
    assertEquals(CivilStandard.KOREA, LunisolarPreset.INSTANT.applyTo(korea).civil)
  }

  @Test
  fun instantCivilNamesIsInstant() {
    assertEquals(LunisolarPreset.INSTANT, LunisolarPreset.of(LunisolarConfig(monthBoundary = MonthBoundary.INSTANT, monthTable = MonthTable.CIVIL_NAMES)))
    assertNull(LunisolarPreset.of(LunisolarConfig(monthTable = MonthTable.DERIVED)))
  }

  @Test
  fun title() {
    assertEquals("合朔那一刻換月", LunisolarPreset.INSTANT.getTitle(Lang.ZH_TW))
    assertEquals("合朔那一刻换月", LunisolarPreset.INSTANT.getTitle(Lang.ZH_CN))
  }
}

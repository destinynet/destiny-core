/**
 * Created by smallufo on 2026-08-18.
 */
package destiny.core.astrology

import destiny.core.BirthDataNamePlace
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.Location
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * [resolveGrain] —— 讓只吃 [IBirthDataNamePlace] 的 digester 問得出時刻精度。
 *
 * 動機：[IBirthDataNamePlace] 只是「時刻 + 地點 + 姓名」，不帶精度。多個 digester
 * 因此一律硬編 [BirthDataGrain.MINUTE]，對時辰／日級的資料排出捏造的 ASC 與宮位。
 */
class ResolveGrainTest {

  private val loc = Location.of(25.03, 121.56, "Asia/Taipei")
  private val time: LocalDateTime = LocalDateTime.of(2010, 7, 7, 12, 28)

  private fun plain(): IBirthDataNamePlace = BirthDataNamePlace(Gender.F, time, loc, "Abc", "台北")

  private class Grained(
    private val delegate: IBirthDataNamePlace,
    override val birthDataGrain: BirthDataGrain
  ) : IGrainedBirthData, IBirthDataNamePlace by delegate

  /** 自報精度者，回報自己的 grain。 */
  @Test
  fun grainedReportsItsOwn() {
    BirthDataGrain.entries.forEach { grain ->
      assertEquals(grain, Grained(plain(), grain).resolveGrain())
    }
  }

  /**
   * 不表態者退回 MINUTE。
   *
   * 退回 MINUTE 而非 DAY 是刻意的：事件盤／賽事盤／卜卦盤本來就有精確時刻，
   * 退回 DAY 會讓它們平白失去軸點。
   */
  @Test
  fun plainFallsBackToMinute() {
    assertEquals(BirthDataGrain.MINUTE, plain().resolveGrain())
  }
}

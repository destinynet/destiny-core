package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation.*
import kotlin.test.Test
import kotlin.test.assertSame

internal class LunarStationMonthlyAnimalExplainedTest {

  /**
   * 比對資料： 鍾義明《擇日精萃》 , page 562
   */
  @Test
  fun testMonth() {
    val impl = LunarStationMonthlyAnimalExplained()
    // 日
    assertSame(室, impl.getMonthly(房, 1))
    assertSame(室, impl.getMonthly(虛, 1))
    assertSame(室, impl.getMonthly(昴, 1))
    assertSame(室, impl.getMonthly(星, 1))

    assertSame(柳, impl.getMonthly(房, 12))
    assertSame(柳, impl.getMonthly(虛, 12))
    assertSame(柳, impl.getMonthly(昴, 12))
    assertSame(柳, impl.getMonthly(星, 12))

    // 月
    assertSame(星, impl.getMonthly(心, 1))
    assertSame(星, impl.getMonthly(危, 1))
    assertSame(星, impl.getMonthly(畢, 1))
    assertSame(星, impl.getMonthly(張, 1))

    assertSame(斗, impl.getMonthly(心, 12))
    assertSame(斗, impl.getMonthly(危, 12))
    assertSame(斗, impl.getMonthly(畢, 12))
    assertSame(斗, impl.getMonthly(張, 12))

    // 火
    assertSame(牛, impl.getMonthly(尾, 1))
    assertSame(牛, impl.getMonthly(室, 1))
    assertSame(牛, impl.getMonthly(觜, 1))
    assertSame(牛, impl.getMonthly(翼, 1))

    assertSame(觜, impl.getMonthly(尾, 12))
    assertSame(觜, impl.getMonthly(室, 12))
    assertSame(觜, impl.getMonthly(觜, 12))
    assertSame(觜, impl.getMonthly(翼, 12))

    // 水
    assertSame(參, impl.getMonthly(箕, 1))
    assertSame(參, impl.getMonthly(壁, 1))
    assertSame(參, impl.getMonthly(參, 1))
    assertSame(參, impl.getMonthly(軫, 1))

    assertSame(房, impl.getMonthly(箕, 12))
    assertSame(房, impl.getMonthly(壁, 12))
    assertSame(房, impl.getMonthly(參, 12))
    assertSame(房, impl.getMonthly(軫, 12))

    // 木
    assertSame(心, impl.getMonthly(角, 1))
    assertSame(心, impl.getMonthly(斗, 1))
    assertSame(心, impl.getMonthly(奎, 1))
    assertSame(心, impl.getMonthly(井, 1))

    assertSame(婁, impl.getMonthly(角, 12))
    assertSame(婁, impl.getMonthly(斗, 12))
    assertSame(婁, impl.getMonthly(奎, 12))
    assertSame(婁, impl.getMonthly(井, 12))

    // 金
    assertSame(胃, impl.getMonthly(亢, 1))
    assertSame(胃, impl.getMonthly(牛, 1))
    assertSame(胃, impl.getMonthly(婁, 1))
    assertSame(胃, impl.getMonthly(鬼, 1))

    assertSame(軫, impl.getMonthly(亢, 12))
    assertSame(軫, impl.getMonthly(牛, 12))
    assertSame(軫, impl.getMonthly(婁, 12))
    assertSame(軫, impl.getMonthly(鬼, 12))

    // 土
    assertSame(角, impl.getMonthly(氐, 1))
    assertSame(角, impl.getMonthly(女, 1))
    assertSame(角, impl.getMonthly(胃, 1))
    assertSame(角, impl.getMonthly(柳, 1))

    assertSame(危, impl.getMonthly(氐, 12))
    assertSame(危, impl.getMonthly(女, 12))
    assertSame(危, impl.getMonthly(胃, 12))
    assertSame(危, impl.getMonthly(柳, 12))
  }
}

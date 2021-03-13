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
    assertSame(室, impl.getMonthlyStation(房, 1))
    assertSame(室, impl.getMonthlyStation(虛, 1))
    assertSame(室, impl.getMonthlyStation(昴, 1))
    assertSame(室, impl.getMonthlyStation(星, 1))

    assertSame(柳, impl.getMonthlyStation(房, 12))
    assertSame(柳, impl.getMonthlyStation(虛, 12))
    assertSame(柳, impl.getMonthlyStation(昴, 12))
    assertSame(柳, impl.getMonthlyStation(星, 12))

    // 月
    assertSame(星, impl.getMonthlyStation(心, 1))
    assertSame(星, impl.getMonthlyStation(危, 1))
    assertSame(星, impl.getMonthlyStation(畢, 1))
    assertSame(星, impl.getMonthlyStation(張, 1))

    assertSame(斗, impl.getMonthlyStation(心, 12))
    assertSame(斗, impl.getMonthlyStation(危, 12))
    assertSame(斗, impl.getMonthlyStation(畢, 12))
    assertSame(斗, impl.getMonthlyStation(張, 12))

    // 火
    assertSame(牛, impl.getMonthlyStation(尾, 1))
    assertSame(牛, impl.getMonthlyStation(室, 1))
    assertSame(牛, impl.getMonthlyStation(觜, 1))
    assertSame(牛, impl.getMonthlyStation(翼, 1))

    assertSame(觜, impl.getMonthlyStation(尾, 12))
    assertSame(觜, impl.getMonthlyStation(室, 12))
    assertSame(觜, impl.getMonthlyStation(觜, 12))
    assertSame(觜, impl.getMonthlyStation(翼, 12))

    // 水
    assertSame(參, impl.getMonthlyStation(箕, 1))
    assertSame(參, impl.getMonthlyStation(壁, 1))
    assertSame(參, impl.getMonthlyStation(參, 1))
    assertSame(參, impl.getMonthlyStation(軫, 1))

    assertSame(房, impl.getMonthlyStation(箕, 12))
    assertSame(房, impl.getMonthlyStation(壁, 12))
    assertSame(房, impl.getMonthlyStation(參, 12))
    assertSame(房, impl.getMonthlyStation(軫, 12))

    // 木
    assertSame(心, impl.getMonthlyStation(角, 1))
    assertSame(心, impl.getMonthlyStation(斗, 1))
    assertSame(心, impl.getMonthlyStation(奎, 1))
    assertSame(心, impl.getMonthlyStation(井, 1))

    assertSame(婁, impl.getMonthlyStation(角, 12))
    assertSame(婁, impl.getMonthlyStation(斗, 12))
    assertSame(婁, impl.getMonthlyStation(奎, 12))
    assertSame(婁, impl.getMonthlyStation(井, 12))

    // 金
    assertSame(胃, impl.getMonthlyStation(亢, 1))
    assertSame(胃, impl.getMonthlyStation(牛, 1))
    assertSame(胃, impl.getMonthlyStation(婁, 1))
    assertSame(胃, impl.getMonthlyStation(鬼, 1))

    assertSame(軫, impl.getMonthlyStation(亢, 12))
    assertSame(軫, impl.getMonthlyStation(牛, 12))
    assertSame(軫, impl.getMonthlyStation(婁, 12))
    assertSame(軫, impl.getMonthlyStation(鬼, 12))

    // 土
    assertSame(角, impl.getMonthlyStation(氐, 1))
    assertSame(角, impl.getMonthlyStation(女, 1))
    assertSame(角, impl.getMonthlyStation(胃, 1))
    assertSame(角, impl.getMonthlyStation(柳, 1))

    assertSame(危, impl.getMonthlyStation(氐, 12))
    assertSame(危, impl.getMonthlyStation(女, 12))
    assertSame(危, impl.getMonthlyStation(胃, 12))
    assertSame(危, impl.getMonthlyStation(柳, 12))
  }
}

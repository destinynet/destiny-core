/**
 * Created by smallufo on 2019-10-19.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.iching.Symbol
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartPatternTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testChartPatternNames() {
    ChartPattern.合十(MntDir.向).also {
      assertEquals("合十", it.getName())
    }

    ChartPattern.伏吟元旦盤(MntDir.向).also {
      assertEquals("伏吟元旦盤", it.getName())
    }

    ChartPattern.反吟(MntDir.向).also {
      assertEquals("反吟", it.getName())
    }

    ChartPattern.父母三般卦.also {
      assertEquals("父母三般卦", it.getName())
    }

    ChartPattern.連珠三般卦.also {
      assertEquals("連珠三般卦", it.getName())
    }

    ChartPattern.七星打劫(Symbol.離 , emptyMap()).also {
      assertEquals("七星打劫", it.getName())
    }

    ChartPattern.八純卦.also {
      assertEquals("八純卦", it.getName())
    }

  }


  @Test
  fun testBlockPatternNames() {
    BlockPattern.合十(MntDir.向).also {
      assertEquals("合十", it.getName())
    }

    BlockPattern.伏吟元旦盤(MntDir.山).also {
      assertEquals("伏吟元旦盤", it.getName())
    }

    BlockPattern.伏吟天盤(MntDir.山).also {
      assertEquals("伏吟天盤", it.getName())
    }

    BlockPattern.反吟元旦盤(MntDir.向).also {
      assertEquals("反吟元旦盤", it.getName())
    }
  }
}

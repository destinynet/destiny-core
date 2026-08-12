/**
 * Created by smallufo on 2022-03-01.
 */
package destiny.core.calendar.eightwords

import destiny.core.EnumTest
import destiny.tools.Lang
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ReactionTest : EnumTest() {

  @Test
  fun test() {
    testEnums(Reaction::class, false)
  }

  /** 縮寫要能以平台中性的 [Lang] 取得 —— 呈現層 DTO 不得依賴 java.util.Locale */
  @Test
  fun abbreviation_byLang() {
    assertEquals("殺", Reaction.七殺.getAbbreviation(Lang.ZH_TW))
    assertEquals("才", Reaction.偏財.getAbbreviation(Lang.ZH_TW))
    assertEquals("财", Reaction.正財.getAbbreviation(Lang.ZH_CN))
  }

}

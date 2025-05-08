/**
 * Created by smallufo on 2025-05-08.
 */
package destiny.core.misc

import destiny.tools.KotlinLogging
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class NumerologyToolsTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testNumerology1990() {
    val birthDate = LocalDate.of(1990, 5, 15)
    val currentYear = 2025

    val numerology = NumerologyTools.getPythagoreanNumerology(birthDate, currentYear)
    logger.info("numerology is: $numerology")

    assertEquals(3, numerology.lifePathNumber, "生命靈數應為 3: 1+9+9+0=19, 1+9=10, 1+0=1, 5=5, 1+5=6, 1+5+6=12, 1+2=3")
    assertEquals(6, numerology.birthdayNumber, "生日數字應為 6: 15, 1+5=6")
    assertEquals(2, numerology.personalYearNumber, "個人年數應為 2: 5+1+5+2+0+2+5=20, 2+0=2")


    // 挑戰數
    val expectedChallenges = listOf(1, 5, 4, 4)
    assertEquals(expectedChallenges, numerology.challengeNumbers, "挑戰數驗證失敗")

    // 高潮數
    val expectedPinnacles = listOf(11, 7, 9, 6)
    assertEquals(expectedPinnacles, numerology.pinnacleNumbers, "高潮數驗證失敗")
  }
}

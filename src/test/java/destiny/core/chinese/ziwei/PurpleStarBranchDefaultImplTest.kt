/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch.午
import destiny.core.chinese.Branch.未
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class PurpleStarBranchDefaultImplTest {

  private val logger = LoggerFactory.getLogger(javaClass)


  internal var impl: IPurpleStarBranch = PurpleStarBranchDefaultImpl()

  @Test
  fun testTitle() {
    assertNotNull(impl.getTitle(Locale.TAIWAN))
    assertNotNull(impl.getTitle(Locale.SIMPLIFIED_CHINESE))
    logger.info("tw = {} , cn = {}", impl.getTitle(Locale.TAIWAN), impl.getTitle(Locale.CHINA))
  }

  /**
   * 計算紫微星所在宮位 , 驗證資料見此教學頁面 http://bit.ly/2oo2hZz
   */
  @Test
  fun getBranchOfPurpleStar() {
    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午, impl.getBranchOfPurpleStar(4, 23))

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未, impl.getBranchOfPurpleStar(4, 24))
  }

}
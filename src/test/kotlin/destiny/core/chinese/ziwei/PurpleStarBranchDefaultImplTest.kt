/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.午
import destiny.core.chinese.Branch.未
import kotlin.test.Test
import kotlin.test.assertSame

class PurpleStarBranchDefaultImplTest {

  internal var impl: IPurpleStarBranch = PurpleStarBranchDefaultImpl()

  /**
   * 計算紫微星所在宮位 , 驗證資料見此教學頁面 http://bit.ly/2oo2hZz
   */
  @Test
  fun getBranchOfPurpleStar() {
    // 假設某個人出生日是23日，五行局為金四局 ==> 紫微在午
    assertSame(午, impl.getBranchOfPurpleStarNonLeap(4, 23))

    // 假設有一個人是24日生，金四局 ==> 紫微在未
    assertSame(未, impl.getBranchOfPurpleStarNonLeap(4, 24))

    /**
     * http://bit.ly/2A93c3g
     * 以男命陰歷1963年9月20日卯時生人(即癸卯年生)為例
     * 【1】20(生日)除以局數(火六局)=3(商數)..2(餘數)
     * 【2】餘數不為零→A= 3(商數)+1=4，
     *     B=局數(火六局)─2(餘數)=4
     * 【3】從寅宮順數(A=4)至巳宮後，因B為偶數則從巳宮之下一宮午宮，
     * 開始順數(B=4)至酉宮，所以紫微星在酉宮。
     */
    assertSame(Branch.酉 , impl.getBranchOfPurpleStarNonLeap(6 , 20) )
  }
}

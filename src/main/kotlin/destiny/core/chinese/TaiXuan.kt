/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import java.io.Serializable

/**
 * 太玄數
 *
 * 甲己子午九，乙庚丑未八，丙辛寅申七，丁壬卯酉六，戊癸辰戌五，巳亥單四數
 * 深入探討資料： @see [http://tieba.baidu.com/p/2236977909](http://tieba.baidu.com/p/2236977909)
 */
class TaiXuan : Serializable {
  companion object {

    private val stemMap = mapOf(
      甲 to 9,
      乙 to 8,
      丙 to 7,
      丁 to 6,
      戊 to 5,
      己 to 9,
      庚 to 8,
      辛 to 7,
      壬 to 6,
      癸 to 5
    )

    private val branchMap = mapOf(
      子 to 9,
      丑 to 8,
      寅 to 7,
      卯 to 6,
      辰 to 5,
      巳 to 4,
      午 to 9,
      未 to 8,
      申 to 7,
      酉 to 6,
      戌 to 5,
      亥 to 4
    )


    operator fun get(stem: Stem): Int {
      return stemMap.getValue(stem)
    }

    operator fun get(branch: Branch): Int {
      return branchMap.getValue(branch)
    }
  }
}

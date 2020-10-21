/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:06:07
 */
package destiny.core.chinese.eightwords

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import java.io.Serializable


/**
 * 地支藏干標準實作
 * 潘文欽有另一套說法：
 * http://destiny.to/ubbthreads/files/1704571-%E8%97%8F%E5%B9%B2.jpg
 */
class HiddenStemsStandardImpl : IHiddenStems, Serializable {

  override fun getHiddenStems(branch: Branch): List<Stem> {
    return map.getValue(branch)
  }

  companion object {

    val map = mapOf(
      子 to listOf(癸),
      丑 to listOf(己, 癸, 辛),
      寅 to listOf(甲, 丙, 戊),
      卯 to listOf(乙),
      辰 to listOf(戊, 乙, 癸),
      巳 to listOf(丙, 戊, 庚),
      午 to listOf(丁, 己),
      未 to listOf(己, 丁, 乙),
      申 to listOf(庚, 壬, 戊),
      酉 to listOf(辛),
      戌 to listOf(戊, 辛, 丁),
      亥 to listOf(壬, 甲))
  }

}

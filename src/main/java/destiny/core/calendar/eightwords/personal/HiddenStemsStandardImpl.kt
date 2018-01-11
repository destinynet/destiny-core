/**
 * @author smallufo
 * @date 2005/4/7
 * @time 下午 02:06:07
 */
package destiny.core.calendar.eightwords.personal

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import java.io.Serializable


/**
 * 地支藏干標準實作
 * 潘文欽有另一套說法：
 * http://destiny.to/ubbthreads/files/1704571-%E8%97%8F%E5%B9%B2.jpg
 */
class HiddenStemsStandardImpl : IHiddenStems, Serializable {

  override fun getHiddenStems(branch: Branch): List<Stem> {
    return map[branch]!!
  }

  companion object {

    val map = mapOf(
      Branch.子 to listOf(Stem.癸),
      Branch.丑 to listOf(Stem.己, Stem.癸, Stem.辛),
      Branch.寅 to listOf(Stem.甲, Stem.丙, Stem.戊),
      Branch.卯 to listOf(Stem.乙),
      Branch.辰 to listOf(Stem.戊, Stem.乙, Stem.癸),
      Branch.巳 to listOf(Stem.丙, Stem.戊, Stem.庚),
      Branch.午 to listOf(Stem.丁, Stem.己),
      Branch.未 to listOf(Stem.己, Stem.丁, Stem.乙),
      Branch.申 to listOf(Stem.庚, Stem.壬, Stem.戊),
      Branch.酉 to listOf(Stem.辛),
      Branch.戌 to listOf(Stem.戊, Stem.辛, Stem.丁),
      Branch.亥 to listOf(Stem.壬, Stem.甲))
  }

}

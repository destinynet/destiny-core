/**
 * Created by smallufo on 2015-05-12.
 */
package destiny.core.chinese

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
      Stem.甲 to 9,
      Stem.乙 to 8,
      Stem.丙 to 7,
      Stem.丁 to 6,
      Stem.戊 to 5,
      Stem.己 to 9,
      Stem.庚 to 8,
      Stem.辛 to 7,
      Stem.壬 to 6,
      Stem.癸 to 5
    )

    private val branchMap = mapOf(
      Branch.子 to 9,
      Branch.丑 to 8,
      Branch.寅 to 7,
      Branch.卯 to 6,
      Branch.辰 to 5,
      Branch.巳 to 4,
      Branch.午 to 9,
      Branch.未 to 8,
      Branch.申 to 7,
      Branch.酉 to 6,
      Branch.戌 to 5,
      Branch.亥 to 4
    )


    operator fun get(stem: Stem): Int {
      return stemMap[stem]!!
    }

    operator fun get(branch: Branch): Int {
      return branchMap[branch]!!
    }
  }
}

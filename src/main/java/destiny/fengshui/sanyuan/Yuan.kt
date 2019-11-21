/**
 * Created by smallufo on 2015-05-13.
 */
package destiny.fengshui.sanyuan

import kotlin.math.abs

enum class Yuan {
  UP,  // 上元 , since 1864 立春
  MID, // 中元 , since 1924 立春
  LOW; // 下元 , since 1984 立春


  companion object {
    /**
     * 從西元的年份推算，當時是哪元 . NOTE : 必須是立春後
     * @param year : 為 proleptic year .
     * 1=西元元年
     * 0=西元前1年
     * -1=西元前2年
     **/
    fun getYuan(year: Int): Yuan {
      val gap180 = (year - 1864).let {
        if (it >= 0)
          it
        else
          180 - abs(it) % 180
      } % 180

      return when (gap180 / 60 + 1) {
        1 -> UP
        2 -> MID
        else -> LOW
      }
    }
  }
}



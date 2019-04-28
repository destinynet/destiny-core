package destiny.core.calendar


import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅
import destiny.tools.ArrayTools
import java.util.*

enum class SolarTerms constructor(val zodiacDegree: Int) {
  立春(315),
  雨水(330),
  驚蟄(345),
  春分(0),
  清明(15),
  穀雨(30),
  立夏(45),
  小滿(60),
  芒種(75),
  夏至(90),
  小暑(105),
  大暑(120),
  立秋(135),
  處暑(150),
  白露(165),
  秋分(180),
  寒露(195),
  霜降(210),
  立冬(225),
  小雪(240),
  大雪(255),
  冬至(270),
  小寒(285),
  大寒(300);

  /**
   * 此「節氣」是否是「節」
   * 立春 => true
   * 雨水 => false
   * 驚蟄 => true
   * ...
   */
  val major: Boolean
    get() = getIndex(this) % 2 == 0

  /** 取得地支  */
  val branch: Branch
    get() {
      val index = getIndex(this)
      return 寅.next(index / 2)
    }

  operator fun next(): SolarTerms {
    return get(getIndex(this) + 1)
  }

  fun previous(): SolarTerms {
    return get(getIndex(this) - 1)
  }


  override fun toString(): String {
    return name
  }

  companion object {

    /**
     * @param solarTerm 節氣
     * @return 傳回 index , 立春為 0 , 雨水為 1 , ... , 大寒 為 23
     */
    fun getIndex(solarTerm: SolarTerms): Int {
      return Arrays.binarySearch(values(), solarTerm)
    }

    fun of(branch: Branch) : List<SolarTerms> {
      return values().filter { it.branch == branch }.toList()
    }

    /**
     * @param solarTermsIndex 節氣的索引
     * @return 0 傳回立春 , 1 傳回 雨水 , ... , 23 傳回 大寒 , 接著連續 24 傳回立春
     */
    operator fun get(solarTermsIndex: Int): SolarTerms {
      return ArrayTools[values(), solarTermsIndex]
    }

    /**
     * @return 從黃經度數，取得節氣
     */
    fun getFromDegree(degree: Double): SolarTerms {
      var index = degree.toInt() / 15 + 3
      if (index >= 24)
        index -= 24
      return get(index)
    }

    /**
     * 上一個「節」、下一個「節」
     * 立春 , 驚蟄 , 清明 ...
     */
    fun getPrevNextMajorSolarTerms(currentSolarTerms: SolarTerms): Pair<SolarTerms, SolarTerms> {
      return if (currentSolarTerms.major) {
        Pair(currentSolarTerms, currentSolarTerms.next().next())
      } else {
        Pair(currentSolarTerms.previous(), currentSolarTerms.next())
      }
    }


    /**
     * 取得下一個「節」
     *
     * @param currentSolarTerms 現在的「節」
     * @param reverse           是否逆推
     * @return 下一個「節」（如果 reverse == true，則傳回上一個「節」）
     */
    fun getNextMajorSolarTerms(currentSolarTerms: SolarTerms, reverse: Boolean): SolarTerms {
      val currentSolarTermsIndex = getIndex(currentSolarTerms)
      return if (currentSolarTermsIndex % 2 == 0) {
        //立春 , 驚蟄 , 清明 ...
        if (!reverse)
        //順推
          currentSolarTerms.next().next()
        else
          currentSolarTerms
      } else {
        //雨水 , 春分 , 穀雨 ...
        if (!reverse)
        //順推
          currentSolarTerms.next()
        else
          currentSolarTerms.previous()
      }
    } // getNextMajorSolarTerms()
  }

}

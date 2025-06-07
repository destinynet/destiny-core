package destiny.core.calendar


import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.寅

enum class SolarTerms(val zodiacDegree: Int) {
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
    get() = ordinal % 2 == 0

  /** 取得地支  */
  val branch: Branch
    get() = 寅.next(ordinal / 2)


  operator fun next(): SolarTerms {
    return get(ordinal + 1)
  }

  fun previous(): SolarTerms {
    return get(ordinal - 1)
  }


  override fun toString(): String {
    return name
  }

  companion object {
    private val branchToSolarTermsMap: Map<Branch, List<SolarTerms>> by lazy {
      entries.groupBy { it.branch }
    }

    /**
     * @param solarTerm 節氣
     * @return 傳回 index , 立春為 0 , 雨水為 1 , ... , 大寒 為 23
     */
    fun getIndex(solarTerm: SolarTerms): Int = solarTerm.ordinal


    fun of(branch: Branch) : List<SolarTerms> {
      return branchToSolarTermsMap[branch] ?: emptyList()
    }

    /**
     * @param solarTermsIndex 節氣的索引
     * @return 0 傳回立春 , 1 傳回 雨水 , ... , 23 傳回 大寒 , 接著連續 24 傳回立春
     */
    operator fun get(solarTermsIndex: Int): SolarTerms {
      return entries[solarTermsIndex.mod(entries.size)]
    }

    /**
     * @return 從黃經度數，取得節氣
     */
    fun getFromDegree(degree: Double): SolarTerms {
      val normalizedDegree = degree.mod(360.0)
      val index = ((normalizedDegree.toInt() / 15) + 3).mod(24)
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
      return when {
        currentSolarTerms.major && !reverse  -> currentSolarTerms.next().next()
        currentSolarTerms.major && reverse   -> currentSolarTerms
        !currentSolarTerms.major && !reverse -> currentSolarTerms.next()
        else                                 -> currentSolarTerms.previous()
      }
    }
  }

}

package destiny.core.calendar

object Constants {

  const val SECONDS_OF_DAY = 86400L

  /** 1970-01-01 */
  object UnixEpoch {

    /** Julian Day of 1970-01-01 */
    const val JULIAN_DAY = 2440587.5

    /** 從 Julian Day 到 1970-01-01 總共有幾秒 */
    const val JULIAN_SECONDS = (JULIAN_DAY * SECONDS_OF_DAY).toLong()

    /** 承上 , 有幾毫秒 */
    const val JULIAN_MILLI_SECONDS = JULIAN_SECONDS * 1000L
  }

  /** 1582-10-15 */
  object CutOver1582 {
    /**
     * Julian Calendar    終止於西元 1582-10-04 , 該日的 Julian Day 是 2299159.5
     * Gregorian Calendar 開始於西元 1582-10-15 , 該日的 Julian Day 是 2299160.5
     */
    const val JULIAN_DAY = 2299160.5

    /**
     * UNIX 開始日，往前推算到 Gregorian 開始日 (西元 1582-10-15) , 倒數 141427 整天
     */
    private const val FROM_UNIXEPOCH_DAYS = -141427

    /** 承上 , 距離秒數 : -12219292800 */
    const val FROM_UNIXEPOCH_SECONDS = FROM_UNIXEPOCH_DAYS * SECONDS_OF_DAY

    /** 承上 , 距離多少 milliSeconds */
    const val FROM_UNIXEPOCH_MILLI_SECONDS = FROM_UNIXEPOCH_SECONDS * 1000L
  }

  /** 西元元年1月1日 (Julian Calendar) */
  object JulianYear1 {

    const val JULIAN_DAY = 1721423.5

    /** 從 1970-01-01 往前數 719164 天，就是 (J) 西元元年1月1日 */
    const val FROM_UNIXEPOCH_DAYS = -719164

  }
}

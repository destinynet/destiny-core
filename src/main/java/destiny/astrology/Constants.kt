package destiny.astrology

object Constants {
  /** 平均日  */
  val MEAN_DAY = (24 * 60 * 60).toDouble() //86400
  /** 恆星日 , 23h 56m 4s  */
  val SIDEREAL_DAY = (23 * 60 * 60).toDouble() + (56 * 60).toDouble() + 4.09074 //86 164.0907

  /** 平年 (Gregorian Year , 365 or 366)  */
  val MEAN_YEAR = 86400 * 365.0000    //31536000
  /** 回歸年 (From one equinox to the next) */
  val TROPICAL_YEAR = 86400 * 365.242190  //31556925.2
  /** 恆星年 (Between maximum elevation passages of a fixed star) */
  val SIDEREAL_YEAR = 86400 * 365.256363  //31558149.8

  /** 恆星月 (1994-2000)  */
  val SIDEREAL_MONTH = 86400 * 27.321662 // 2360591.6
  /** 會合月 (新月到新月)  */
  val SYNODICAL_MONTH = 86400 * 29.530589 // 2551442.89
  /** 回歸月  */
  val TROPICAL_MONTH = 86400 * 27.321582 // 2360584.68
  /** 異常月 (通過近地點 , perigee) , 1994-2000  */
  val ANOMALISTIC_MONTH = 86400 * 27.554550 // 2380713.12
  /** 龍月 ? (The Draconic month is the time between one node passage to the next. ) */
  val DRACONIC_MONTH = 86400 * 27.212221 // 2351135.89

}
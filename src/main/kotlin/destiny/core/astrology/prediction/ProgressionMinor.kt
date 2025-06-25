package destiny.core.astrology.prediction

import destiny.core.astrology.Constants

/**
 * Minor Progressions 一月一年法 , 月限法
 *
 * Puka : 月限法（Minor Progressions）：此方法為美國光之教堂（Church of Light）占星家C.C.Zain（本名Elbert Benjamin）所創，
 * 以出生後一個太陰月等於實際人生一年，依據C.C.Zain學派的系統，月限法是用來查看中期事件趨勢，
 * 次限法用來查看長期事件趨勢，短期事件趨勢則以流年法的引動為主。光之教堂稱次限法為主要推運（Major Progressions），
 * 月限法為次要推運（Minor Progressions）。Zain並認為只查看次限法加上流年法是不足的，必須加上月限才可看出人生的起伏軌跡。
 */
class ProgressionMinor(override val forward: Boolean = true) : AbstractProgression() {

  override val type: ProgressionType = ProgressionType.MINOR

  /** MP (一月一年) , 分子是 一年  */
  override val numerator: Double = Constants.SIDEREAL_YEAR_SECONDS

  /** MP (一月一年) , 分母是 一月  */
  override val denominator: Double = Constants.SIDEREAL_MONTH

}

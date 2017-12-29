/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

/**
 * 將 [IntAge] 計算出來的結果 Tuple2[GMT , GMT] 附註年份
 * 例如，西元年份、或是民國年份、或是中國歷史紀元
 */
interface IntAgeNote : Descriptive {

  /** 此時刻的註記 ( 通常只註記「西元XX年」 )  */
  fun getAgeNote(gmtJulDay: Double): String?

  /**
   * @param startAndEnd [from GMT, to GMT] 時刻
   */
  fun getAgeNote(startAndEnd: Pair<Double, Double>): String?
}

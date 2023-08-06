package destiny.core.calendar

import destiny.core.calendar.Constants.SECONDS_OF_DAY
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.absoluteValue

data class SolarTermsTimePos(
  /** 目前時刻 */
  val gmtJulDay : GmtJulDay,

  /** 前一個（也是目前）的「節」, 及其 GMT JulDay */
  val prevMajor: SolarTermsEvent,

  /** 下一個「中氣」，及其 GMT JulDay */
  val middle: SolarTermsEvent,

  /** 下一個「節」， 及其 GMT JulDay */
  val nextMajor: SolarTermsEvent) : Serializable {

  /** 前半部 (節 to 中氣) */
  val firstHalf : Boolean by lazy {
    gmtJulDay < middle.begin
  }

  /** 後半部 (中氣 to 節) */
  val secondHalf : Boolean by lazy {
    !firstHalf
  }

  /** 取得「節」或「氣」, 若在前半部，則取 [prevMajor] , 若在後半部，則取 [middle] */
  val solarTerms : SolarTerms by lazy {
    if (firstHalf)
      prevMajor.solarTerms
    else
      middle.solarTerms
  }

  /** 距離「節」的開始有幾秒 */
  val toPrevMajorSeconds : Double by lazy {
    (gmtJulDay - prevMajor.begin) * SECONDS_OF_DAY
  }

  /** 距離「節」的節數有幾秒 */
  val toNextMajorSeconds : Double by lazy {
    (nextMajor.begin - gmtJulDay) * SECONDS_OF_DAY
  }

  /** 距離「中氣」有幾秒 , 這裡採用絕對值，必須配合 [firstHalf] 或是 [secondHalf] 才能知道是在「中氣」之前還是之後 */
  val toMiddleSeconds : Double by lazy {
    (middle.begin - gmtJulDay).absoluteValue
  }

  override fun toString(): String {
    return StringBuilder().apply {
      append("距離 ").append(prevMajor.solarTerms).append("節 (${prevMajor.solarTerms.branch})有")
      append((gmtJulDay - prevMajor.begin).toString().take(5))
      append("日")

      append("，")

      append("距離 ").append(nextMajor.begin).append("節 (${nextMajor.solarTerms.branch})有")
      append((nextMajor.begin-gmtJulDay).toString().take(5))
      append("日；")

      append("在中氣").append(middle.solarTerms)
      if (firstHalf) {
        append("之前")
      } else {
        append("之後")
      }
      append(abs(middle.begin - gmtJulDay).toString().take(5)).append("日。")
    }.toString()

  }
}

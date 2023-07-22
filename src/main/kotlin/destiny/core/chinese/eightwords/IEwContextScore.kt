/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.Scale
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.SolarTermsTimePos
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsContextModel
import destiny.tools.getTitle
import mu.KotlinLogging
import java.util.*


interface IEwContextScore : java.io.Serializable {

  /**
   * 總分
   */
  fun getScore(ewContext: IEightWordsContextModel): Double {
    return getScoreMap(ewContext).map { it.value.first + it.value.second }.sum()
  }

  /**
   * 每柱的干支各貢獻幾分
   */
  fun getScoreMap(ewContext: IEightWordsContextModel): Map<Scale, Pair<Double, Double>> {
    return Scale.entries.associateWith { scale ->
      getPillarScore(scale, ewContext.eightWords, ewContext.gmtJulDay, ewContext.solarTermsTimePos)
    }.onEach { (scale, score) ->
      logger.trace { "\t${scale.getTitle(Locale.TAIWAN)} 貢獻 $score 分" }
    }
  }

  /**
   * 取得此柱的干支分別貢獻幾分
   */
  fun getPillarScore(scale: Scale, eightWords: IEightWords, gmtJulDay: GmtJulDay, solarTermsTimePos: SolarTermsTimePos): Pair<Double, Double>


  companion object {
    val logger = KotlinLogging.logger { }
  }

}

/**
 * Created by smallufo on 2022-07-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
import mu.KotlinLogging


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
    return Scale.values().associateWith { scale ->
      getPillarScore(scale, ewContext)
    }.onEach { (scale, score) ->
      logger.trace { "\t${scale.toChineseChar()} 貢獻 $score 分" }
    }
  }

  /**
   * 取得此柱的干支分別貢獻幾分
   */
  fun getPillarScore(scale: Scale, ewContext: IEightWordsContextModel): Pair<Double, Double>


  companion object {
    val logger = KotlinLogging.logger { }
  }

}
